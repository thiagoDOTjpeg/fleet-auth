import { Pool } from 'pg';
import amqp from 'amqplib';
import dotenv from 'dotenv';

dotenv.config();

const DATABASE_URL = process.env.DATABASE_URL;
const RABBIT_URL = process.env.RABBITMQ_URL || 'amqp://guest:guest@localhost:5672';
const EXCHANGE_NAME = 'exchange.user';
const BATCH_SIZE = 10;
const POLL_INTERVAL_MS = 2000;

if (!DATABASE_URL) {
    console.error("❌ ERRO: DATABASE_URL não definida.");
    process.exit(1);
}

const pool = new Pool({
    connectionString: DATABASE_URL,
});

async function startWorker() {
    console.log('🚀 [Worker Bun] Iniciando sistema de Outbox...');

    let channel: amqp.Channel;
    try {
        const connection = await amqp.connect(RABBIT_URL);
        channel = await connection.createChannel();
        await channel.assertExchange(EXCHANGE_NAME, 'topic', { durable: true });
        console.log(`✅ [RabbitMQ] Conectado e Exchange '${EXCHANGE_NAME}' verificada.`);
    } catch (error) {
        console.error("❌ [RabbitMQ] Falha na conexão:", error);
        process.exit(1);
    }

    while (true) {
        const client = await pool.connect();

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
                console.log(`📦 [Batch] Processando ${events.length} eventos...`);
                const processedIds: string[] = [];

                for (const event of events) {
                    try {
                        const payloadStr = typeof event.payload === 'string'
                            ? event.payload
                            : JSON.stringify(event.payload);

                        channel.publish(EXCHANGE_NAME, event.type, Buffer.from(payloadStr));

                        processedIds.push(event.id);
                        console.log(`   -> Enviado: ${event.type} (ID: ${event.id})`);
                    } catch (pubError) {
                        console.error(`   ❌ Falha envio ID ${event.id}:`, pubError);
                    }
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

            if (events.length === 0) {
                await Bun.sleep(POLL_INTERVAL_MS);
            }

        } catch (err) {
            await client.query('ROLLBACK');
            console.error('🔥 [Erro Crítico] Rollback executado:', err);
            await Bun.sleep(5000);
        } finally {
            client.release();
        }
    }
}

startWorker();