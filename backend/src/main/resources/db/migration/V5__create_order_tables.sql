CREATE TABLE customer_order (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    order_number VARCHAR(32) NOT NULL,
    customer_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    subtotal NUMERIC(19, 4) NOT NULL CHECK (subtotal >= 0),
    tax NUMERIC(19, 4) NOT NULL CHECK (tax >= 0),
    total NUMERIC(19, 4) NOT NULL CHECK (total >= 0),
    idempotency_key VARCHAR(100) NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_order_tenant_number UNIQUE (tenant_id, order_number),
    CONSTRAINT uk_order_tenant_idempotency UNIQUE (tenant_id, idempotency_key),
    CONSTRAINT chk_order_status CHECK (status IN ('CREATED', 'PAYMENT_PENDING', 'CONFIRMED', 'PROCESSING', 'READY_FOR_DISPATCH', 'DISPATCHED', 'IN_TRANSIT', 'DELIVERED', 'PAYMENT_FAILED', 'CANCELLED', 'DELIVERY_FAILED', 'RETURN_INITIATED', 'RETURNED')),
    CONSTRAINT chk_order_currency CHECK (currency ~ '^[A-Z]{3}$')
);

CREATE TABLE order_item (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    order_id UUID NOT NULL REFERENCES customer_order(id) ON DELETE CASCADE,
    sku VARCHAR(100) NOT NULL,
    description VARCHAR(300) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(19, 4) NOT NULL CHECK (unit_price >= 0),
    line_total NUMERIC(19, 4) NOT NULL CHECK (line_total >= 0)
);

CREATE INDEX idx_order_tenant_created ON customer_order (tenant_id, created_at DESC);
