CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE refresh_tokens ADD session_id UUID;
ALTER TABLE refresh_tokens ADD COLUMN version BIGINT DEFAULT 0;

CREATE INDEX idx_refresh_tokens_session_id ON refresh_tokens(session_id);