# FleetPulse – Event Catalog

**Status:** Draft candidate event catalog – event names/payloads require approval against HLD workflow and service boundaries  
**Revision:** 1.1

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
| `TenantCreated` | Tenant | Reporting, Notification | Tenant onboarded |
| `CustomerCreated` | Customer | Order, reporting | Customer profile created |
| `CustomerUpdated` | Customer | Order, reporting | Customer profile changed |
| `OrderCreated` | Order | Inventory, Payment, Reporting | Order accepted |
| `InventoryReservationRequested` | Order | Inventory | Reserve required stock |
| `InventoryReserved` | Inventory | Order | Stock successfully reserved |
| `InventoryRejected` | Inventory | Order | Reservation failed |
| `PaymentRequested` | Order | Payment | Simulated payment attempt requested |
| `PaymentCompleted` | Payment | Order, Reporting, Notification | Simulated payment completed successfully (real payment processing is out of scope for v1) |
| `PaymentFailed` | Payment | Order, Notification | Simulated payment failed |
| `ShipmentCreated` | Order (or approved shipment owner) | Delivery, Tracking, Reporting, Notification | Shipment record created; ownership to be confirmed |
| `DeliveryAssignmentRequested` | Order / Delivery workflow (owner TBD) | Delivery | Find eligible delivery partner |
| `DeliveryAssigned` | Delivery | Order, Tracking, Notification | Delivery partner assigned |
| `DeliveryAssignmentFailed` | Delivery | Order, Notification | No eligible partner assigned |
| `OrderReadyForDelivery` | Order | Delivery, Notification, Reporting | Order has completed the preceding fulfilment steps and is ready for delivery assignment |
| `ShipmentStatusChanged` | Order (or approved shipment owner) | Notification, Reporting | Shipment state changed; owner to be confirmed |
| `LocationUpdated` | Tracking | Reporting / alert rules | New GPS position received |
| `ShipmentDelivered` | Delivery (or approved shipment owner) | Notification, Reporting | Delivery completed; owner to be confirmed |
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
