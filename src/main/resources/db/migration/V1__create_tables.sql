CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS payments (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id  VARCHAR(50)    NOT NULL,
    amount      DECIMAL(19, 2) NOT NULL,
    currency    VARCHAR(3)     NOT NULL,
    description VARCHAR(500),
    status      VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    version     BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_payments_account_id ON payments (account_id);
CREATE INDEX idx_payments_status ON payments (status);
CREATE INDEX idx_payments_created_at ON payments (created_at);
