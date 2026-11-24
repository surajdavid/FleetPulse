# FleetPulse – Sequence Diagrams

**Status:** Proposed workflows – review required  
**Version:** 1.0

## 1. Create order and reserve inventory

```mermaid
sequenceDiagram
  actor Customer
  participant UI as React Client
  participant GW as API Gateway
  participant OS as Order/Shipment
  participant DB as Order DB
  participant Broker as Event Broker
  participant WS as Warehouse
  Customer->>UI: Submit order
  UI->>GW: POST /api/v1/orders
  GW->>OS: Authenticated request + tenant context
  OS->>DB: Validate and persist order + outbox
  DB-->>OS: Commit
  OS-->>GW: 202 Accepted / order status
  GW-->>UI: Order created/pending
  OS->>Broker: Publish OrderCreated
  Broker->>WS: Deliver order event
  WS->>WS: Reserve inventory idempotently
  WS->>Broker: InventoryReserved or ReservationRejected
  Broker->>OS: Deliver reservation outcome
  OS->>DB: Update shipment/order state
```

## 2. Payment simulation

```mermaid
sequenceDiagram
  participant OS as Order/Shipment
  participant Broker as Event Broker
  participant PS as Payment Service
  participant PDB as Payment DB
  OS->>Broker: PaymentRequested
  Broker->>PS: PaymentRequested
  PS->>PDB: Create idempotent payment attempt
  PS->>PS: Run deterministic simulator outcome
  PS->>PDB: Persist result
  PS->>Broker: PaymentSucceeded or PaymentFailed
  Broker->>OS: Deliver payment result
  OS->>OS: Apply valid state transition
```

## 3. Partner assignment

```mermaid
sequenceDiagram
  participant OS as Order/Shipment
  participant Broker as Event Broker
  participant AS as Allocation Service
  participant ADB as Allocation DB
  OS->>Broker: PartnerAssignmentRequested
  Broker->>AS: Deliver request
  AS->>ADB: Find eligible partner and reserve assignment
  ADB-->>AS: Assignment result
  alt Partner found
    AS->>Broker: PartnerAssigned
  else No partner found
    AS->>Broker: PartnerAssignmentFailed
  end
  Broker->>OS: Deliver result
  OS->>OS: Update shipment state
```

## 4. GPS tracking update

```mermaid
sequenceDiagram
  actor Partner
  participant Client as Partner Client
  participant GW as API Gateway
  participant TS as Tracking Service
  participant TDB as Tracking DB
  participant Broker as Event Broker
  Partner->>Client: Share current location
  Client->>GW: POST location update
  GW->>TS: Authenticated request + tenant context
  TS->>TS: Validate partner, shipment and coordinates
  TS->>TDB: Persist location update
  TS-->>Client: 202 Accepted
  TS->>Broker: LocationUpdated
```

## 5. Important workflow guarantees

- Every write command is authorized and tenant-scoped.
- Repeated client requests must not create duplicate orders/payments/assignments when an idempotency key is required.
- Event consumers must handle duplicate delivery.
- State transitions are validated by the owning service.
- Long-running workflows expose intermediate states and failure reasons.
- Compensation behavior (release stock, refund simulation, unassign partner) must be defined before the corresponding workflow is considered complete.
