# FleetPulse – Database Design

**Status:** Logical proposal – validate against approved BRD/SRS and HLD  
**Version:** 1.0

## 1. Data ownership

Each microservice owns its database/schema and is the only service allowed to write it. Cross-service references use opaque IDs; do not create cross-service foreign keys. A service may maintain a local read copy of external reference data where needed.

Proposed logical stores:
- Identity/Tenant: tenants, users, memberships, roles.
- Customer: customers, addresses.
- Order/Shipment: orders, order items, shipments, shipment status history.
- Warehouse/Inventory: warehouses, inventory items, stock balances, reservations.
- Payment: payment attempts and transactions.
- Allocation: delivery partners, availability, assignments.
- Tracking: tracking sessions and location updates.
- Notification: notification records and delivery attempts.
- Reporting: denormalized tenant-scoped projections.

## 2. Common conventions

- Primary keys: UUIDs unless the implementation has a documented reason otherwise.
- Include `tenant_id` on every tenant-owned row.
- Use `created_at`, `updated_at` in UTC; use `deleted_at` only where soft deletion is justified.
- Use optimistic version columns for concurrently updated aggregates where appropriate.
- Store money as `NUMERIC(19,4)` (or a documented currency-specific scale), never floating point.
- Store currency as ISO 4217 code alongside amount.
- Store timestamps with timezone semantics; normalize to UTC.
- Add constraints for valid quantities, amounts and status values.
- Index tenant-scoped access patterns, commonly `(tenant_id, id)` and `(tenant_id, created_at)`.
- Avoid storing derived totals unless consistency is maintained transactionally.

## 3. Core logical entities

### Tenant and identity
`tenant(id, name, status, created_at, updated_at)`  
`user_account(id, email, password_hash_or_external_subject, status, created_at)`  
`tenant_membership(id, tenant_id, user_id, status, created_at)`  
`role(id, tenant_id_nullable, name)`  
`membership_role(membership_id, role_id)`

Unique constraints: normalized email according to identity policy; `(tenant_id, user_id)` membership; role naming scope.

### Customer
`customer(id, tenant_id, customer_code, name, email, phone, status, created_at, updated_at)`  
`customer_address(id, tenant_id, customer_id, address_type, line1, line2, city, state, postal_code, country_code, latitude_nullable, longitude_nullable)`

Use tenant-scoped uniqueness for customer code. Validate coordinates and country/postal formats at the application boundary.

### Order and shipment
`customer_order(id, tenant_id, order_number, customer_id, status, currency, subtotal, tax, total, idempotency_key, created_at, updated_at)`  
`order_item(id, tenant_id, order_id, sku, description, quantity, unit_price, line_total)`  
`shipment(id, tenant_id, shipment_number, order_id, origin_warehouse_id, destination_address_snapshot, status, assigned_partner_id_nullable, created_at, updated_at, version)`  
`shipment_status_history(id, tenant_id, shipment_id, old_status, new_status, changed_by, reason, occurred_at)`

Address snapshot preserves the destination as it was when the shipment was created. External IDs are not foreign keys into other services.

### Warehouse and inventory
`warehouse(id, tenant_id, code, name, address, status)`  
`inventory_item(id, tenant_id, warehouse_id, sku, quantity_on_hand, quantity_reserved, version)`  
`inventory_reservation(id, tenant_id, shipment_id, warehouse_id, sku, quantity, status, expires_at, created_at)`

Enforce `quantity_on_hand >= 0`, `quantity_reserved >= 0`, and reserved quantity not exceeding on-hand quantity through transactional business logic plus database constraints where possible.

### Payment
`payment_transaction(id, tenant_id, order_id, amount, currency, status, provider_reference, idempotency_key, created_at, updated_at)`  
Do not store PAN, CVV or bank credentials. Simulator responses must be clearly marked as simulated.

### Partner allocation
`delivery_partner(id, tenant_id, display_name, contact_reference, status)`  
`partner_availability(id, tenant_id, partner_id, availability_status, effective_from, effective_to)`  
`shipment_assignment(id, tenant_id, shipment_id, partner_id, status, assigned_at, accepted_at_nullable, completed_at_nullable)`

### Tracking
`tracking_session(id, tenant_id, shipment_id, partner_id, status, started_at, ended_at_nullable)`  
`location_update(id, tenant_id, tracking_session_id, latitude, longitude, accuracy_m_nullable, recorded_at, received_at)`

Index `(tenant_id, tracking_session_id, recorded_at DESC)`. Consider time partitioning/retention only after volume is measured.

### Notification and reporting
`notification(id, tenant_id, recipient_ref, channel, template_key, status, idempotency_key, created_at)`  
`notification_attempt(id, tenant_id, notification_id, provider, status, attempted_at, error_code_nullable)`  
Reporting tables are projections, rebuildable from retained events or source exports where feasible.

## 4. Tenant isolation

- Tenant context comes from authenticated identity, never blindly from client input.
- All repository queries must include tenant scope.
- Consider database row-level security as defense in depth after validating connection-pool tenant-context behavior.
- Add cross-tenant negative tests for every tenant-owned resource.
- Background consumers must derive tenant from the trusted event envelope and validate it.
- Never expose sequential identifiers that enable cross-tenant enumeration.

## 5. Transaction and concurrency rules

- Use a single local transaction for an aggregate update and its outbox record.
- Use optimistic locking or atomic conditional updates for inventory and assignment races.
- Define unique idempotency constraints in the database.
- Do not use a distributed transaction across service databases.
- Document compensating actions for failed multi-service workflows.

## 6. Indexing, retention and migrations

Create indexes from query patterns, not speculatively. Use versioned migrations (for example, Flyway or Liquibase; select one). Production migrations must be backward-compatible with the currently running application during rolling deployments. Define retention and deletion policies for GPS history, notification data and personal data before production.
