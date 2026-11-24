# FleetPulse
FleetPulse is a multi-tenant logistics and delivery management SaaS platform designed to help logistics companies manage their complete delivery operations through a centralized digital system.  The platform will connect customers, logistics administrators, warehouse managers, delivery partners and customer support agents.

## Backend local setup

The backend uses Java 21, PostgreSQL, Flyway, Kafka, and a JWT resource server. Start local dependencies with `docker compose up -d`, then run the Spring Boot application from `backend/` with Maven. Configure `FLEETPULSE_JWT_ISSUER_URI` to the issuer that signs access tokens and `FLEETPULSE_JWT_AUDIENCE` to the API audience. The API validates signature, issuer, audience, and token time claims. Tokens must include a UUID `tenant_id` claim; the API derives tenant scope from that verified claim.

The customer API is available under `/api/v1/customers`. It supports create, list, get, and patch operations. Access tokens need `customers:read` or `customers:write` scopes and a UUID `tenant_id` claim. List pagination defaults to 20 records and accepts sizes from 1 through 100. Health checks are exposed at `/actuator/health`.

Tenant onboarding and status management are available at `/api/v1/tenants`; tenant creation and suspension require the `platform:admin` scope. Tenant members are added with the `tenants:members:write` scope. Customer addresses are managed under `/api/v1/customers/{customerId}/addresses`.

Orders can be created at `/api/v1/orders` with an `Idempotency-Key`, a tenant-owned customer and destination address, line items, currency, and tax amount. Order reads are scoped to the authenticated tenant. The order aggregate enforces the BRD lifecycle transitions and uses decimal monetary fields.

Order creation writes an `OrderCreated` event to the transactional outbox. The publisher retries failed Kafka sends with bounded exponential delays; consumers should deduplicate by event ID as required by the event catalog.

Warehouse and inventory APIs support warehouse creation, stock receipts/issues, tenant-scoped inventory reads, idempotent reservations, and reservation release. Stock changes use conditional database updates so concurrent reservations cannot take available stock below zero. A suspended tenant is blocked from customer and inventory operations.

Payments can be simulated at `/api/v1/payments` with `SUCCESS` or `FAILURE` outcomes. Requests must match an existing tenant order's total and currency, and require an idempotency key. Payment responses and events are marked simulated; the implementation stores no payment credentials and performs no financial transactions.

Delivery operations include driver registration/availability, idempotent shipment assignment, validated assignment state transitions, and S3-reference proof-of-delivery submission. Driver assignment is tenant-scoped and prevents concurrent active assignments for one shipment.

Delivery-partner GPS updates are accepted at `/api/v1/tracking/location-updates` only when the JWT `driver_id` is assigned to the shipment. Validated updates are retained in PostgreSQL, cached in Redis, emitted to Kafka, and sent over tenant-scoped STOMP topics at `/ws`; tracking reads return the latest 100 updates.

Database settings can be overridden with `FLEETPULSE_DATABASE_URL`, `FLEETPULSE_DATABASE_USERNAME`, and `FLEETPULSE_DATABASE_PASSWORD`. Flyway owns schema creation; Hibernate validates the resulting schema at startup.
