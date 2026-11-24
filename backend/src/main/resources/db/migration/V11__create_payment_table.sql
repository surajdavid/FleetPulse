CREATE TABLE payment_transaction (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    order_id UUID NOT NULL,
    amount NUMERIC(19, 4) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),
    status VARCHAR(24) NOT NULL,
    transaction_reference VARCHAR(40) NOT NULL UNIQUE,
    provider_reference VARCHAR(80),
    simulated BOOLEAN NOT NULL CHECK (simulated = TRUE),
    idempotency_key VARCHAR(100) NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_payment_tenant_idempotency UNIQUE (tenant_id, idempotency_key),
    CONSTRAINT chk_payment_status CHECK (status IN ('CREATED', 'PENDING', 'SUCCESSFUL', 'FAILED', 'CANCELLED', 'REFUND_PENDING', 'REFUNDED'))
);

CREATE INDEX idx_payment_tenant_order ON payment_transaction (tenant_id, order_id, created_at DESC);
