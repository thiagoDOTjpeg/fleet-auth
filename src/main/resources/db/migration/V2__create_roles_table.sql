CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

INSERT INTO roles (name) VALUES
                             ('ROLE_ADMIN'),
                             ('ROLE_SUPPORT'),
                             ('ROLE_USER'),
                             ('ROLE_DRIVER'),
                             ('ROLE_MERCHANT');
