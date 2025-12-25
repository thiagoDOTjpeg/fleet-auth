CREATE TABLE auth.outbox_events (
                                    id UUID PRIMARY KEY,
                                    aggregate_type VARCHAR(50) NOT NULL,
                                    aggregate_id VARCHAR(255) NOT NULL,
                                    type VARCHAR(100) NOT NULL,
                                    payload JSONB NOT NULL,
                                    processed BOOLEAN NOT NULL DEFAULT FALSE,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_outbox_processed_created ON auth.outbox_events (processed, created_at);