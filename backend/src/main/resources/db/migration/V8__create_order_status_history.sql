CREATE TABLE order_status_history (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    order_id UUID NOT NULL REFERENCES customer_order(id) ON DELETE CASCADE,
    old_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    changed_by VARCHAR(200) NOT NULL,
    reason VARCHAR(500),
    occurred_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_order_history_tenant_order ON order_status_history (tenant_id, order_id, occurred_at);

CREATE TABLE shipment_status_history (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    shipment_id UUID NOT NULL REFERENCES shipment(id) ON DELETE CASCADE,
    old_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    changed_by VARCHAR(200) NOT NULL,
    reason VARCHAR(500),
    occurred_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_shipment_history_tenant_shipment ON shipment_status_history (tenant_id, shipment_id, occurred_at);
