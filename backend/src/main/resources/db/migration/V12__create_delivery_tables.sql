CREATE TABLE driver (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    contact_reference VARCHAR(200) NOT NULL,
    status VARCHAR(16) NOT NULL,
    CONSTRAINT chk_driver_status CHECK (status IN ('AVAILABLE', 'BUSY', 'INACTIVE'))
);

CREATE TABLE delivery_assignment (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    shipment_id UUID NOT NULL,
    driver_id UUID NOT NULL REFERENCES driver(id),
    status VARCHAR(24) NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL,
    accepted_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    proof_of_delivery_reference VARCHAR(500),
    failure_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_assignment_tenant_idempotency UNIQUE (tenant_id, idempotency_key),
    CONSTRAINT chk_assignment_status CHECK (status IN ('ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED', 'REJECTED', 'FAILED', 'RESCHEDULED', 'RETURNED'))
);

CREATE UNIQUE INDEX uk_active_assignment_per_shipment ON delivery_assignment (tenant_id, shipment_id)
    WHERE status IN ('ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'RESCHEDULED');
CREATE INDEX idx_driver_available ON driver (tenant_id, status);
CREATE INDEX idx_assignment_tenant_driver ON delivery_assignment (tenant_id, driver_id, assigned_at DESC);
