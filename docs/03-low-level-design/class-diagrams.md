# FleetPulse – Class Diagrams

**Status:** Illustrative domain model – implementation detail may evolve  
**Revision:** 1.1

> These are domain-level diagrams, not generated Java class diagrams. Keep service boundaries intact; do not create cross-service entity relationships.

## 1. Order and shipment domain

```mermaid
classDiagram
  class CustomerOrder {
    +UUID id
    +UUID tenantId
    +String orderNumber
    +OrderStatus status
    +Money total
    +create()
    +changeStatus()
  }
  class OrderItem {
    +UUID id
    +String sku
    +int quantity
    +Money unitPrice
  }
  class Shipment {
    +UUID id
    +UUID tenantId
    +String shipmentNumber
    +ShipmentStatus status
    +int version
    +transitionTo()
  }
  class ShipmentStatusHistory {
    +UUID id
    +ShipmentStatus oldStatus
    +ShipmentStatus newStatus
    +Instant occurredAt
  }
  CustomerOrder "1" *-- "1..*" OrderItem
  CustomerOrder "1" --> "0..*" Shipment
  Shipment "1" *-- "1..*" ShipmentStatusHistory
```

## 2. Warehouse and inventory domain

```mermaid
classDiagram
  class Warehouse {
    +UUID id
    +UUID tenantId
    +String code
    +WarehouseStatus status
  }
  class InventoryItem {
    +UUID id
    +String sku
    +long quantityOnHand
    +long quantityReserved
    +long version
    +reserve(quantity)
    +release(quantity)
  }
  class InventoryReservation {
    +UUID id
    +UUID shipmentId
    +long quantity
    +ReservationStatus status
    +confirm()
    +release()
  }
  Warehouse "1" *-- "*" InventoryItem
  InventoryItem "1" --> "*" InventoryReservation
```

## 3. Delivery and tracking domain

```mermaid
classDiagram
  class Driver {
    +UUID id
    +String displayName
    +DriverStatus status
  }
  class DeliveryAssignment {
    +UUID id
    +UUID shipmentId
    +AssignmentStatus status
    +assign()
    +accept()
    +complete()
  }
  class TrackingSession {
    +UUID id
    +UUID shipmentId
    +TrackingStatus status
  }
  class LocationUpdate {
    +UUID id
    +double latitude
    +double longitude
    +Instant recordedAt
  }
  Driver "1" --> "*" DeliveryAssignment
  TrackingSession "1" *-- "*" LocationUpdate
```

## 4. Identity, payment and support (additional domain sketches)

```mermaid
classDiagram
  class User {
    +UUID id
    +String identityReference
    +UserStatus status
  }
  class Role {
    +UUID id
    +String name
  }
  class Tenant {
    +UUID id
    +String name
    +TenantStatus status
  }
  class Membership {
    +UUID id
    +UUID tenantId
    +MembershipStatus status
  }
  class Payment {
    +UUID id
    +UUID tenantId
    +UUID orderId
    +Money amount
    +PaymentStatus status
    +boolean simulated
  }
  class SupportTicket {
    +UUID id
    +UUID tenantId
    +TicketStatus status
    +TicketPriority priority
  }
  class SupportComment {
    +UUID id
    +UUID ticketId
    +Instant createdAt
  }
  Tenant "1" --> "*" Membership
  User "1" --> "*" Membership
  User "*" --> "*" Role
  SupportTicket "1" *-- "*" SupportComment
```

These are illustrative classes only. Identity/tenant relationship and role assignment details must follow the approved IAM design. Payment is simulated in v1; no real payment provider credentials or settlement objects are modeled. Notification templates/records, audit records, customer addresses and stock movements also require detailed class-level design before implementation.

## 5. Application-layer pattern

Within each service, a typical structure is:

- `controller`: transport concerns, request/response DTOs.
- `application`: use cases and transaction boundaries.
- `domain`: entities, value objects, invariants and domain services.
- `infrastructure`: repositories, messaging adapters and external clients.
- `config`: security, persistence and observability configuration.

Keep domain objects independent of HTTP and broker-specific types. Use DTOs at API boundaries and explicit event payload types at messaging boundaries.
