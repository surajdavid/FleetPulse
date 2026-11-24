# FleetPulse – Sequence Diagrams

**Status:** Illustrative workflow proposals – aligned to HLD sequence; Saga ownership and compensation remain open  
**Revision:** 1.1

## 1. Create order and reserve inventory

```mermaid
sequenceDiagram
  actor Customer
  participant UI as React Client
  participant GW as Spring Cloud API Gateway
  participant OS as Order Service
  participant DB as Order DB
  participant Broker as Apache Kafka
  participant WS as Inventory Service
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
  WS->>Broker: InventoryReserved or InventoryRejected
  Broker->>OS: Deliver reservation outcome
  OS->>DB: Update shipment/order state
```

## 2. Payment simulation

```mermaid
sequenceDiagram
  participant OS as Order Service
  participant Broker as Apache Kafka
  participant PS as Payment Service (simulator)
  participant PDB as Payment DB
  OS->>Broker: PaymentRequested
  Broker->>PS: PaymentRequested
  PS->>PDB: Create idempotent payment attempt
  PS->>PS: Produce configured simulated success/failure outcome
  PS->>PDB: Persist result
  PS->>Broker: PaymentCompleted or PaymentFailed
  Broker->>OS: Deliver payment result
  OS->>OS: Apply valid state transition
```

## 3. Partner assignment

```mermaid
sequenceDiagram
  participant OS as Order Service
  participant Broker as Apache Kafka
  participant AS as Delivery Service
  participant ADB as Delivery persistence
  OS->>Broker: PartnerAssignmentRequested
  Broker->>AS: Deliver request
  AS->>ADB: Find eligible partner and reserve assignment
  ADB-->>AS: Assignment result
  alt Partner found
    AS->>Broker: DeliveryAssigned
  else No partner found
    AS->>Broker: DeliveryAssignmentFailed
  end
  Broker->>OS: Deliver result
  OS->>OS: Update shipment state
```

## 4. End-to-end order fulfilment (HLD workflow)

```mermaid
sequenceDiagram
  actor Customer
  participant GW as API Gateway
  participant O as Order Service
  participant K as Apache Kafka
  participant I as Inventory Service
  participant P as Payment Service (simulator)
  participant D as Delivery Service
  participant N as Notification Service
  Customer->>GW: Submit order
  GW->>O: Create order
  O->>O: Validate and persist order
  O-->>Customer: Order accepted / pending
  O->>K: OrderCreated
  K->>I: Reserve inventory
  I->>K: InventoryReserved or InventoryRejected
  K->>P: Initiate simulated payment (when workflow permits)
  P->>K: PaymentCompleted or PaymentFailed
  K->>O: Workflow result
  O->>K: OrderReadyForDelivery (after required steps succeed)
  K->>D: Create delivery assignment
  D->>K: DeliveryAssigned
  K->>N: Relevant business events
  N-->>Customer: Send notification
```

The HLD labels this workflow illustrative. Exact step ordering, payment timing, orchestration owner, failure paths and compensations remain open and must be reconciled with the SRS before implementation.

## 5. GPS tracking update

```mermaid
sequenceDiagram
  actor Partner
  participant Client as Partner Client
  participant GW as Spring Cloud API Gateway
  participant TS as Tracking Service
  participant TDB as Tracking persistence
  participant REDIS as Redis
  participant Broker as Apache Kafka
  participant WS as WebSocket layer
  actor Viewer as Authorized Customer / Operations Client
  Partner->>Client: Share current location
  Client->>GW: POST location update
  GW->>TS: Authenticated request + tenant context
  TS->>TS: Validate partner, shipment and coordinates
  TS->>TDB: Persist durable tracking history
  TS->>REDIS: Update latest location
  TS-->>Client: Accepted
  TS->>Broker: Publish tracking event
  TS->>WS: Publish location update
  WS-->>Viewer: Deliver update after tenant/resource authorization
```

## 6. Important workflow guarantees

- Every write command is authorized and tenant-scoped.
- Repeated client requests must not create duplicate orders/payments/assignments when an idempotency key is required.
- Event consumers must handle duplicate delivery.
- State transitions are validated by the owning service.
- Long-running workflows expose intermediate states and failure reasons.
- Compensation behavior (release stock, refund simulation, unassign partner) must be defined before the corresponding workflow is considered complete.
