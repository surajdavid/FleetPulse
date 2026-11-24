CREATE TABLE tenant_membership (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    user_id UUID NOT NULL,
    role VARCHAR(24) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_tenant_membership_user UNIQUE (tenant_id, user_id),
    CONSTRAINT chk_membership_role CHECK (role IN ('TENANT_ADMIN', 'CUSTOMER', 'DELIVERY_PARTNER', 'WAREHOUSE_MANAGER', 'SUPPORT_AGENT')),
    CONSTRAINT chk_membership_status CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);

CREATE INDEX idx_membership_user ON tenant_membership (user_id);
