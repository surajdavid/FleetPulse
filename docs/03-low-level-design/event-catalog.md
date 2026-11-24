# FleetPulse – Event Catalog

**Status:** Proposed event contract – review required  
**Version:** 1.0

## 1. Event rules

- Events describe facts that have occurred; commands request work.
- Use past-tense names and explicit schema versions.
- Every event includes `eventId`, `eventType`, `eventVersion`, `occurredAt`, `producer`, `tenantId`, `aggregateId`, `correlationId` and `payload`.
- Publish through a transactional outbox where a database change and event must be atomic.
- Consumers are idempotent and tolerate duplicate delivery.
- Partition/order by aggregate key when per-aggregate ordering is required.
- Avoid personal data and secrets in payloads.

## 2. Initial catalog

| Event | Producer | Main consumers | Meaning |
|---|---|---|---|
| `TenantCreated` | Identity/Tenant | Reporting, notification | Tenant onboarded |
| `CustomerCreated` | Customer | Order, reporting | Customer profile created |
| `CustomerUpdated` | Customer | Order, reporting | Customer profile changed |
| `OrderCreated` | Order/Shipment | Warehouse, Payment, Reporting | Order accepted |
| `InventoryReservationRequested` | Order/Shipment | Warehouse | Reserve required stock |
| `InventoryReserved` | Warehouse | Order/Shipment | Stock successfully reserved |
| `InventoryReservationRejected` | Warehouse | Order/Shipment | Reservation failed |
| `PaymentRequested` | Order/Shipment | Payment | Payment attempt requested |
| `PaymentSucceeded` | Payment | Order/Shipment, Reporting, Notification | Simulated/real payment succeeded |
| `PaymentFailed` | Payment | Order/Shipment, Notification | Payment failed |
| `ShipmentCreated` | Order/Shipment | Tracking, Reporting, Notification | Shipment record created |
| `PartnerAssignmentRequested` | Order/Shipment | Allocation | Find eligible partner |
| `PartnerAssigned` | Allocation | Order/Shipment, Tracking, Notification | Partner assigned |
| `PartnerAssignmentFailed` | Allocation | Order/Shipment, Notification | No partner assigned |
| `ShipmentStatusChanged` | Order/Shipment | Notification, Reporting | Shipment state changed |
| `LocationUpdated` | Tracking | Reporting / alert rules | New GPS position received |
| `ShipmentDelivered` | Order/Shipment | Notification, Reporting | Delivery completed |
| `NotificationDeliveryFailed` | Notification | Operations/Reporting | Provider delivery failed |

## 3. Example payload

```json
{
  "eventId": "uuid",
  "eventType": "InventoryReserved",
  "eventVersion": 1,
  "occurredAt": "2026-01-01T10:00:00Z",
  "producer": "warehouse-service",
  "tenantId": "tenant-uuid",
  "aggregateType": "InventoryReservation",
  "aggregateId": "reservation-uuid",
  "correlationId": "correlation-uuid",
  "causationId": "request-event-uuid",
  "payload": {
    "shipmentId": "shipment-uuid",
    "warehouseId": "warehouse-uuid",
    "items": [{"sku": "SKU-001", "quantity": 2}]
  }
}
```

## 4. Topic/routing organization

A proposed topic convention is `fleetpulse.<domain>.<event-name>.v1`, or a smaller set of domain topics with event type in the envelope. Select one convention and document partition keys, retention and ACLs before implementation. Do not create a topic per tenant.

## 5. Failure handling

- Retry transient failures with bounded exponential backoff.
- Send repeatedly failing messages to a dead-letter topic/queue.
- Alert on dead-letter growth and consumer lag.
- Provide an audited replay tool/process.
- Track processed event IDs or use naturally idempotent state transitions.
- Schema changes must be backward-compatible or published as a new version.
