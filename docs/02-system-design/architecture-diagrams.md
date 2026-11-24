# FleetPulse – Architecture Diagrams

**Status:** Draft – aligned to BRD/SRS v1.0 and HLD v1.0; pending team approval  
**Revision:** 1.1  
**Source documents:** FP-DOC-001 (BRD/SRS), FP-DOC-002 (HLD)

> These diagrams reflect the source documents' proposed logical architecture. They do not freeze service deployment topology, database topology, AWS service mapping, or unresolved workflow decisions.

## 1. System context

```mermaid
flowchart LR
    PA[Platform Administrator] --> WEB[React Web Application]
    TA[Tenant Administrator] --> WEB
    C[Customer] --> WEB
    WM[Warehouse Manager] --> WEB
    DP[Delivery Partner] --> CLIENT[Delivery Partner Client / GPS Simulator]
    SA[Support Agent] --> WEB
    WEB --> GW[Spring Cloud API Gateway]
    CLIENT --> GW
    GW --> FP[FleetPulse Platform]
    FP --> EMAIL[Email / Notification Provider - TBD]
    FP --> S3[AWS S3 - documents / proof of delivery]
```

The BRD/SRS defines a web interface with desktop and mobile-browser layouts. A native mobile application and real payment processing are out of scope for v1. GPS coordinates are submitted by a delivery partner client application or simulator; an external GPS/maps provider is not specified.

## 2. Logical service architecture

```mermaid
flowchart TB
  subgraph Clients
    WEB[React + TypeScript Web Application]
    DRIVER[Delivery Partner Client / GPS Simulator]
  end
  WEB --> GW[API Gateway - Spring Cloud Gateway]
  DRIVER --> GW
  GW --> ID[Identity Service]
  GW --> TEN[Tenant Service]
  GW --> CUS[Customer Service]
  GW --> ORD[Order Service]
  GW --> PAY[Payment Service - simulation only]
  GW --> INV[Inventory Service]
  GW --> DEL[Delivery Service]
  GW --> TRK[Tracking Service]
  GW --> NOT[Notification Service]
  GW --> SUP[Support Service]
  GW --> REP[Reporting Service]
  ID <--> TEN
  ID --> PG[(PostgreSQL - service-owned persistence)]
  TEN --> PG
  CUS --> PG
  ORD --> PG
  PAY --> PG
  INV --> PG
  DEL --> PG
  TRK --> PG
  NOT --> PG
  SUP --> PG
  REP --> PG
  ORD <--> K[(Apache Kafka)]
  PAY <--> K
  INV <--> K
  DEL <--> K
  TRK <--> K
  CUS <--> K
  NOT <--> K
  SUP <--> K
  REP <--> K
  TRK --> REDIS[(Redis - latest / short-lived location state)]
  TRK --> WS[WebSocket / STOMP]
  WS --> WEB
  DEL --> S3[AWS S3 - documents / proof of delivery]
```

This is a logical view, not a deployment diagram. The HLD lists API Gateway plus Identity, Tenant, Customer, Order, Payment, Inventory, Delivery, Tracking, Notification, Support and Reporting. It does not approve combining these services into larger deployables. Physical database separation versus schemas in a shared PostgreSQL cluster remains open; the diagram therefore shows logical ownership, not a decided number of database instances.

## 3. GPS tracking flow

```mermaid
flowchart LR
  CLIENT[Delivery Partner Client / Simulator] --> GW[API Gateway]
  GW --> TS[Tracking Service]
  TS --> AUTH[Validate token, tenant, assignment and coordinates]
  AUTH --> REDIS[(Redis - latest location)]
  AUTH --> PG[(Durable tracking history in PostgreSQL)]
  TS --> K[(Kafka - tracking events)]
  TS --> WS[Authorized WebSocket updates]
  WS --> VIEW[Customer / Operations Web Client]
```

The HLD proposes Redis for the latest location, durable tracking history, Kafka events and WebSocket updates to authorized clients. GPS interval, history retention and detailed protocol behavior remain open for LLD.

## 4. Deployment view (logical)

```mermaid
flowchart TB
  Internet --> EDGE[Edge: TLS / load balancing / WAF]
  EDGE --> GW[API Gateway Container]
  subgraph PrivateRuntime[Private application runtime]
    GW --> SERVICES[Backend service containers]
    SERVICES --> K[Apache Kafka]
    SERVICES --> REDIS[Redis]
    SERVICES --> PG[(PostgreSQL service-owned data)]
    SERVICES --> S3[AWS S3]
  end
  SERVICES --> OBS[Prometheus / Grafana / ELK-compatible logs]
  CI[GitHub Actions] --> REG[Container Registry]
  REG --> PRIVATE_RUNTIME[Deployment target - AWS/Kubernetes mapping TBD]
```

Docker is the containerization baseline and Kubernetes is the stated deployment target; exact AWS service mapping and production topology remain unresolved in the HLD.

## 5. Architecture rules

- Every service owns its business capability and persistence; no direct cross-service table access.
- Use REST for immediate request/response and Kafka for asynchronous business events.
- Use WebSocket for authorized real-time tracking/progress updates.
- Resolve tenant context from trusted authentication context; never trust a request-body tenant ID as authority.
- Keep simulated payment separate from real payment settlement, which is out of scope for v1.
- Use the HLD's transactional outbox guidance for critical database changes that must publish events.
