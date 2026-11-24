CREATE TABLE warehouse (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_warehouse_tenant_code UNIQUE (tenant_id, code),
    CONSTRAINT chk_warehouse_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE inventory_item (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    sku VARCHAR(100) NOT NULL,
    quantity_on_hand BIGINT NOT NULL DEFAULT 0 CHECK (quantity_on_hand >= 0),
    quantity_reserved BIGINT NOT NULL DEFAULT 0 CHECK (quantity_reserved >= 0 AND quantity_reserved <= quantity_on_hand),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_inventory_tenant_warehouse_sku UNIQUE (tenant_id, warehouse_id, sku)
);

CREATE TABLE inventory_reservation (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    shipment_id UUID NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    sku VARCHAR(100) NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    status VARCHAR(16) NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_reservation_status CHECK (status IN ('ACTIVE', 'CONFIRMED', 'RELEASED', 'EXPIRED')),
    CONSTRAINT uk_reservation_tenant_key_sku UNIQUE (tenant_id, idempotency_key, sku)
);

CREATE TABLE stock_movement (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    sku VARCHAR(100) NOT NULL,
    movement_type VARCHAR(24) NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    reference_type VARCHAR(32),
    reference_id UUID,
    occurred_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_stock_movement_type CHECK (movement_type IN ('RECEIPT', 'ISSUE', 'RESERVATION', 'RESERVATION_RELEASE'))
);

CREATE INDEX idx_inventory_tenant_warehouse ON inventory_item (tenant_id, warehouse_id);
CREATE INDEX idx_reservation_tenant_shipment ON inventory_reservation (tenant_id, shipment_id);
CREATE INDEX idx_stock_movement_tenant_warehouse ON stock_movement (tenant_id, warehouse_id, occurred_at DESC);
