CREATE TABLE shipment (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    shipment_number VARCHAR(32) NOT NULL,
    order_id UUID NOT NULL REFERENCES customer_order(id) ON DELETE CASCADE,
    origin_warehouse_id UUID,
    destination_address_snapshot TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_shipment_tenant_number UNIQUE (tenant_id, shipment_number),
    CONSTRAINT chk_shipment_status CHECK (status IN ('CREATED', 'READY_FOR_DISPATCH', 'DISPATCHED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED', 'DELIVERY_FAILED', 'RETURN_INITIATED', 'RETURNED'))
);

CREATE INDEX idx_shipment_tenant_created ON shipment (tenant_id, created_at DESC);
