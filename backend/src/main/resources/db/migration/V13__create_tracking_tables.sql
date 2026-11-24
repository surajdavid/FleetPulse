CREATE TABLE tracking_session (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    shipment_id UUID NOT NULL,
    driver_id UUID NOT NULL,
    status VARCHAR(16) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    ended_at TIMESTAMPTZ,
    CONSTRAINT chk_tracking_status CHECK (status IN ('ACTIVE', 'ENDED'))
);

CREATE UNIQUE INDEX uk_active_tracking_session ON tracking_session (tenant_id, shipment_id) WHERE status = 'ACTIVE';

CREATE TABLE location_update (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    tracking_session_id UUID NOT NULL REFERENCES tracking_session(id) ON DELETE CASCADE,
    latitude DOUBLE PRECISION NOT NULL CHECK (latitude BETWEEN -90 AND 90),
    longitude DOUBLE PRECISION NOT NULL CHECK (longitude BETWEEN -180 AND 180),
    accuracy_m DOUBLE PRECISION CHECK (accuracy_m IS NULL OR accuracy_m >= 0),
    recorded_at TIMESTAMPTZ NOT NULL,
    received_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_location_session_recorded ON location_update (tenant_id, tracking_session_id, recorded_at DESC);
