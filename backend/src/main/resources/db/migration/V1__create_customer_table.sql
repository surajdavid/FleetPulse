CREATE TABLE customer (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    customer_code VARCHAR(64) NOT NULL,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(320),
    phone VARCHAR(32),
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_customer_tenant_code UNIQUE (tenant_id, customer_code),
    CONSTRAINT chk_customer_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_customer_tenant_created ON customer (tenant_id, created_at DESC);
