import amqp from 'amqplib';
import dotenv from 'dotenv';
import { Pool } from 'pg';

dotenv.config();

const DATABASE_URL = process.env.DATABASE_URL;
const RABBIT_URL = process.env.RABBITMQ_URL || 'amqp://guest:guest@localhost:5672';
const EXCHANGE_NAME = 'exchange.user';
const BATCH_SIZE = 10;
const POLL_INTERVAL_MS = 2000;
const MAX_RETRIES = 4;

class Worker {
    private channel: amqp.ConfirmChannel | undefined;
    private connection: amqp.ChannelModel | undefined;
    private pool: Pool;
    private isShuttingDown: boolean = false;

    constructor() {
        if (!DATABASE_URL) {
            console.error("ERRO: DATABASE_URL não definida.");
            process.exit(1);
        }
        this.pool = new Pool({ connectionString: DATABASE_URL })
        this.handleSignal = this.handleSignal.bind(this);
    }

    public handleSignal(signal: string) {
        console.log(`\n[${signal}] Recebido! Iniciando Graceful Shutdown...`);
        console.log("[Shutdown] Parando de aceitar novos batches. Aguardando término do atual...");
        this.isShuttingDown = true;
    }

    private async connectWithRetry(): Promise<amqp.ConfirmChannel> {
        let retries = 0;
        while (retries <= MAX_RETRIES) {
            try {
                const connection = await amqp.connect(RABBIT_URL);
                this.connection = connection;
                const channel = await connection.createConfirmChannel();
                await channel.assertExchange(EXCHANGE_NAME, "topic", { durable: true });

                connection.on("close", () => {
                    console.error("[RabbitMQ] Conexão fechada! Invalidando canal.");
                    this.channel = undefined;
                });

                connection.on("error", (err) => {
                    console.error("[RabbitMQ] Erro na conexão:", err.message);
                    this.channel = undefined;
                });

                console.log(`[RabbitMQ] Conectado.`);
                return channel;
            } catch (error) {
                if (retries >= MAX_RETRIES) throw error;

                const delay = 1000 * Math.pow(2, retries);
                console.log(`[RabbitMQ] Falha na conexão... tentando em ${delay}ms`);
                await new Promise(r => setTimeout(r, delay));
                retries++;
            }
        }
        throw new Error("Falha fatal na conexão RabbitMQ");
    }

    async startWorker() {
        console.log('[Worker Bun] Iniciando...');

        try {
            this.channel = await this.connectWithRetry();
        } catch (e) {
            console.error("Não foi possível iniciar o worker:", e);
            process.exit(1);
        }

        while (!this.isShuttingDown) {
            if (!this.channel) {
                try {
                    console.log("[Loop] Canal inválido detectado antes da transação. Reconectando...");
                    this.channel = await this.connectWithRetry();
                } catch (e) {
                    console.error("[Fatal] Falha ao reconectar no loop.");
                    process.exit(1);
                }
            }
            const client = await this.pool.connect();

            try {
                await client.query('BEGIN');

                const queryText = `
                SELECT id, type, payload 
                FROM auth.outbox_events 
                WHERE processed = false 
                ORDER BY created_at ASC 
                LIMIT $1 
                FOR UPDATE SKIP LOCKED
            `;

                const res = await client.query(queryText, [BATCH_SIZE]);
                const events = res.rows;

                if (events.length > 0) {
                    const processedIds: string[] = [];

                    for (const event of events) {
                        try {
                            const payloadStr = typeof event.payload === 'string'
                                ? event.payload
                                : JSON.stringify(event.payload);

                            if (!this.channel) throw new Error("RABBIT_Connection_Lost");

                            this.channel.publish(EXCHANGE_NAME, event.type, Buffer.from(payloadStr));

                            processedIds.push(event.id);
                            console.log(`-> Enviado: ${event.type} (ID: ${event.id})`);
                        } catch (pubError) {
                            console.error(`Erro ao publicar evento ${event.id}. Abortando batch.`);
                            throw new Error("RABBIT_Connection_Lost");
                        }
                    }

                    try {
                        await this.channel.waitForConfirms();
                    } catch (nackError) {
                        console.error("RabbitMQ recusou uma ou mais mensagens (NACK).");
                        throw new Error("RABBIT_Connection_Lost");
                    }

                    if (processedIds.length > 0) {
                        await client.query(`
                        UPDATE auth.outbox_events 
                        SET processed = true 
                        WHERE id = ANY($1::uuid[])
                    `, [processedIds]);
                    }
                }

                await client.query('COMMIT');

                if (events.length === 0 && !this.isShuttingDown) {
                    await Bun.sleep(POLL_INTERVAL_MS);
                }

            } catch (err: any) {
                await client.query('ROLLBACK');

                if (err.message === "RABBIT_Connection_Lost") {
                    console.warn("[Recuperação] Conexão perdida detectada. Iniciando reconexão...");
                    try {
                        this.channel = await this.connectWithRetry();
                        console.log("[Recuperação] Conexão restabelecida. Retomando processamento.");
                    } catch (fatalError) {
                        console.error("[Fatal] Não foi possível reconectar após retries.", fatalError);
                        process.exit(1);
                    }
                } else {
                    console.error('[Erro Desconhecido]', err);
                    await Bun.sleep(5000);
                }
            } finally {
                client.release();
            }
        }
        await this.cleanup();
    }
    private async cleanup() {
        console.log("[Shutdown] Loop encerrado. Fechando conexões...");

        try {
            if (this.channel) await this.channel.close();
            if (this.connection) await this.connection.close();
            console.log("[Shutdown] RabbitMQ desconectado.");

            await this.pool.end();
            console.log("[Shutdown] Pool PostgreSQL encerrado.");
            process.exit(0);
        } catch (err) {
            console.error("[Shutdown] Erro ao fechar conexões:", err);
            process.exit(1);
        }
    }
}

const worker = new Worker();

const signals = ["SIGINT", "SIGTERM", "SIGQUIT"];

signals.forEach(signal => {
    process.on(signal, () => worker.handleSignal(signal));
});

worker.startWorker();
