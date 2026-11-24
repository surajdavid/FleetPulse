CREATE TABLE customer_address (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    customer_id UUID NOT NULL REFERENCES customer(id),
    address_type VARCHAR(16) NOT NULL,
    line1 VARCHAR(200) NOT NULL,
    line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(24) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    CONSTRAINT chk_address_type CHECK (address_type IN ('HOME', 'BUSINESS', 'OTHER')),
    CONSTRAINT chk_address_country_code CHECK (country_code ~ '^[A-Z]{2}$'),
    CONSTRAINT chk_address_latitude CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),
    CONSTRAINT chk_address_longitude CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180),
    CONSTRAINT chk_address_coordinate_pair CHECK ((latitude IS NULL) = (longitude IS NULL))
);

CREATE INDEX idx_customer_address_tenant_customer ON customer_address (tenant_id, customer_id);
