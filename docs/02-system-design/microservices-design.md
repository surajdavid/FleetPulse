# FleetPulse – Microservices Design

**Status:** Draft – aligned to BRD/SRS v1.0 and HLD v1.0; boundaries remain proposed  
**Revision:** 1.1  
**Source documents:** FP-DOC-001, FP-DOC-002

## 1. Design principles

1. Organize services around the business capabilities named in the HLD.
2. Each service owns its business data and persistence model.
3. Do not allow direct reads/writes to another service's tables.
4. Use REST for immediate interactions and Kafka for asynchronous domain events.
5. Enforce tenant isolation in authentication, service logic and data access.
6. Use idempotency, bounded retries, observability and explicit state transitions.
7. Treat the service catalogue as proposed until the team approves boundaries and deployment units.

## 2. Proposed service catalogue

| Component | Responsibility | Primary data ownership |
|---|---|---|
| API Gateway | Central API entry point, routing and edge concerns | No business data ownership specified |
| Identity Service | Authentication, users, credentials/identity references, roles and token-related functions | User identity and role data (exact split with Tenant Service TBD) |
| Tenant Service | Tenant lifecycle, membership and tenant context support | Tenants and memberships |
| Customer Service | Customer profiles and addresses | Customers and customer addresses |
| Order Service | Order creation, order lifecycle and order-related orchestration | Orders, order items, order lifecycle history |
| Payment Service | Simulated payment attempts, outcomes and simulated refund workflows | Payment records and audit history |
| Inventory Service | Warehouses, stock, reservations and stock movements | Warehouse and inventory records |
| Delivery Service | Delivery partners, assignment, delivery lifecycle and proof-of-delivery references | Partner, assignment and delivery records |
| Tracking Service | GPS ingestion, latest location, tracking history and authorized real-time updates | Tracking sessions and location history |
| Notification Service | Shipment/payment notifications and delivery-attempt tracking | Notification records and attempts |
| Support Service | Support tickets and internal support history | Support tickets and notes |
| Reporting Service | Tenant-scoped operational read models and reports | Reporting projections only; not operational source of truth |

The BRD/HLD identify these service capabilities separately. Whether Identity and Tenant, or any other capabilities, share an initial deployable is not approved by the source documents and must be decided explicitly rather than assumed.

## 3. Service responsibilities and constraints

### API Gateway
- Provide the central API entry point using the proposed Spring Cloud Gateway stack.
- Apply routing and agreed edge controls; downstream services still enforce authorization and tenant ownership.

### Identity and Tenant
- Support secure token-based authentication and role-based access control.
- Support token expiry/refresh according to the authentication design.
- Manage tenant lifecycle, memberships and tenant context.
- Derive tenant context from trusted authentication data, not an untrusted request parameter.
- Open HLD question: custom Spring Security implementation or an external identity provider.

### Customer
- Create, update, retrieve and search tenant-owned customer profiles and addresses.
- Enforce tenant ownership on every operation.

### Order
- Own order state transitions and order lifecycle history.
- Coordinate the fulfilment workflow with Inventory, Payment and Delivery.
- The HLD leaves Saga orchestration ownership open; do not assume a final orchestrator yet.

### Payment
- Implement simulated success/failure and simulated refund workflows only.
- Prevent duplicate payment processing and maintain audit records.
- Do not integrate real settlement or store card credentials; real payment processing is out of scope for v1.

### Inventory
- Manage warehouses, inventory, incoming/outgoing stock movements and reservations.
- Prevent overselling and preserve stock correctness under concurrent requests.

### Delivery
- Manage delivery partner information, eligibility/availability, assignment, delivery progress and proof of delivery.
- Do not claim AI route optimization; it is out of scope for v1.

### Tracking
- Accept GPS updates from an authorized delivery partner client or simulator.
- Validate partner/shipment association, tenant scope, coordinate values and stale updates.
- Use Redis for latest-location/short-lived state as proposed; persist tracking history and publish relevant events.
- Deliver location/progress updates over WebSocket only to authorized clients.
- GPS interval and history retention remain open.

### Notification
- Generate shipment/payment and other required business notifications.
- Record delivery attempts and retry failures according to policy.
- Email/notification provider selection remains open; do not imply a selected vendor or guaranteed SMS/push integration.

### Support
- Support customer ticket creation and support-agent ticket management/history.

### Reporting
- Build tenant-scoped read projections from service events.
- Clearly represent eventual consistency; never become the source of truth for operational records.

## 4. Interaction and consistency rules

- Use REST for immediate queries/commands and Kafka for asynchronous business events.
- Use a versioned event envelope including event ID/type/version, timestamp, tenant ID and correlation context.
- Consumers must be idempotent and tolerate duplicate delivery.
- Use a transactional outbox where a critical local database change and event publication must be atomic.
- Use local ACID transactions; do not use distributed database transactions.
- Evaluate Saga orchestration/compensation for multi-service fulfilment; ownership and compensating actions require approval.

## 5. Persistence boundary

Logical ownership is per service. The physical topology is unresolved: the HLD explicitly asks whether to use separate PostgreSQL databases per microservice or separate schemas in a shared PostgreSQL cluster initially. Do not interpret service ownership as a decision to provision one database server/instance per service.

## 6. Implementation sequencing

The roadmap proposes vertical slices beginning with platform identity/tenant foundations, then customer, order, inventory, simulated payment, delivery allocation, tracking, notification and reporting. Confirm detailed sequencing against dependencies and the two-developer capacity assumption in the BRD/SRS.
