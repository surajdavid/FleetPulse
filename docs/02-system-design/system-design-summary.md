# FleetPulse – System Design Summary

**Document ID:** FP-DOC-007  
**Version:** 1.0  
**Status:** Draft – Pending Team Review  
**Prepared:** November 2026

---

## 1. Introduction

FleetPulse is a multi-tenant SaaS logistics and real-time delivery management platform.

The platform is designed to help businesses manage customers, orders, payments, inventory, deliveries, live GPS tracking, notifications, support, and reporting through a distributed microservices architecture.

This document consolidates the System Design documentation and records the proposed architecture, technology stack, key design decisions, outstanding questions, and next steps.

## 2. System Design Documentation

The following documents form the current System Design package.

| Document ID | Document | Purpose |
|---|---|---|
| FP-DOC-001 | BRD & SRS | Business and software requirements |
| FP-DOC-002 | High-Level Design | Overall system architecture |
| FP-DOC-003 | Architecture Diagrams | Visual representation of architecture and workflows |
| FP-DOC-004 | Microservices Design | Responsibilities and boundaries of services |
| FP-DOC-005 | Communication Design | REST, Kafka, and WebSocket communication |
| FP-DOC-006 | Infrastructure Design | Deployment, cloud, networking, and operations |
| FP-DOC-007 | System Design Summary | Consolidated overview and next steps |

**Current documentation status:** The System Design documents have been drafted. Peer review and approval are pending.

## 3. Proposed Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 21 |
| Backend Framework | Spring Boot |
| Microservices | Spring Cloud |
| API Gateway | Spring Cloud Gateway |
| Security | Spring Security, OAuth2 Resource Server, JWT |
| Frontend | React, TypeScript, Vite |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Database Migration | Flyway |
| Messaging | Apache Kafka |
| Caching | Redis |
| Real-time Communication | WebSocket / STOMP |
| API Documentation | OpenAPI / Swagger |
| Testing | JUnit 5, Mockito, Testcontainers |
| Containerization | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Monitoring | Prometheus, Grafana |
| Logging | ELK-compatible centralized logging |
| Cloud | AWS – proposed target |

## 4. Microservices Architecture

FleetPulse currently has eleven proposed backend microservices.

| # | Microservice | Main Responsibility |
|---|---|---|
| 1 | Identity Service | Authentication and identity management |
| 2 | Tenant Service | Tenant registration and tenant management |
| 3 | Customer Service | Customer information and management |
| 4 | Order Service | Order creation and order lifecycle |
| 5 | Payment Service | Payment workflow; simulated payments in v1 |
| 6 | Inventory Service | Inventory availability and reservation |
| 7 | Delivery Service | Delivery assignment and fulfilment |
| 8 | Tracking Service | GPS ingestion and real-time location tracking |
| 9 | Notification Service | Customer and operational notifications |
| 10 | Support Service | Support requests and issue management |
| 11 | Reporting Service | Business and operational reporting |

Each service owns its business logic and logical data boundary.

Services must not directly access another service's database.

## 5. Communication Architecture

Three primary communication patterns are proposed.

### 5.1 REST APIs

Used for synchronous request-response communication.

Examples:
- Frontend to API Gateway
- API Gateway to backend services
- Required synchronous service-to-service interactions

### 5.2 Apache Kafka

Used for asynchronous event-driven communication.

Illustrative events:
- OrderCreated
- InventoryReserved
- PaymentCompleted
- OrderReadyForDelivery
- DeliveryAssigned
- LocationUpdated

Exact topic names, event contracts, partitions, and consumer groups will be finalized during LLD.

### 5.3 WebSocket

Used for real-time GPS tracking updates.

Proposed tracking flow:

1. Driver application or GPS simulator sends location updates.
2. Tracking Service receives and validates the update.
3. Latest location is stored in Redis.
4. Location history is persisted.
5. Tracking events may be published through Kafka.
6. Authorized users receive updates through WebSocket.

## 6. Data Architecture

**PostgreSQL**
- Durable relational data storage.
- Logical ownership by individual services.
- Database migrations managed through Flyway.

**Redis**
- Caching.
- Latest GPS location.
- Fast access to frequently requested data.

**Apache Kafka**
- Asynchronous event communication.
- Event-driven workflows.
- Decoupling between services.

**Important:** Physical database separation is not yet finalized. The team must decide between separate databases and separate schemas within a shared PostgreSQL infrastructure.

## 7. Core Business Workflows

### 7.1 Order Fulfilment

Proposed workflow:

1. Customer submits an order.
2. API Gateway routes the request to Order Service.
3. Order Service creates the order.
4. OrderCreated event is published.
5. Inventory Service processes inventory reservation.
6. Payment Service processes a simulated payment.
7. The order workflow determines the next state.
8. Delivery Service handles delivery assignment.
9. Notification Service sends relevant notifications.

The exact workflow orchestration and failure-compensation strategy remain open decisions.

### 7.2 Real-Time GPS Tracking

1. Driver sends GPS coordinates.
2. Tracking Service validates incoming data.
3. Redis stores the latest location.
4. Location history is stored durably.
5. Kafka distributes relevant tracking events.
6. WebSocket delivers updates to authorized users.

The proposed GPS update visibility target is P95 below five seconds under normal test conditions. This is a test target, not an approved production SLA.

## 8. Multi-Tenancy and Security

FleetPulse will use shared infrastructure with logical tenant isolation as the initial design direction.

Key requirements:

- Tenant context must come from trusted authentication and authorization.
- A client-provided tenant ID must never be trusted by itself.
- Users must only access data belonging to their authorized tenant.
- API endpoints must enforce authentication and authorization.
- External traffic must use HTTPS.
- Secrets must not be committed to GitHub.
- Sensitive information must not be written to application logs.
- Internal databases, Kafka, and Redis must not be publicly exposed.

Tenant isolation must be enforced at the application and data-access layers.

## 9. Infrastructure and Deployment

The proposed deployment approach:

| Environment | Proposed Infrastructure |
|---|---|
| Local | Docker Compose |
| Development | Shared development environment |
| Staging | Production-like environment |
| Production | Cloud infrastructure with Kubernetes |

AWS is the proposed cloud target.

Candidate AWS services include:
- EKS for Kubernetes
- ECR for container images
- RDS for PostgreSQL
- MSK for Kafka
- ElastiCache for Redis
- S3 for object storage
- Secrets Manager for sensitive configuration
- VPC for network isolation

These are candidate mappings, not approved final selections.

## 10. CI/CD and Observability

### CI/CD

GitHub Actions is the proposed CI/CD platform.

Expected pipeline:

1. Developer creates a pull request or pushes code.
2. Application builds.
3. Automated tests execute.
4. Code quality and security checks run.
5. Docker images are built.
6. Images are published to a registry.
7. Deployment takes place in the target environment.
8. Smoke and acceptance tests execute.
9. Production deployment follows an approved release process.

### Observability

Prometheus and Grafana will support metrics collection and dashboards.

Centralized logging will support troubleshooting and incident investigation.

Important metrics:
- API request rate, errors, and latency
- JVM memory and garbage collection
- CPU and memory utilization
- Database connection pool usage
- Kafka consumer lag
- GPS ingestion latency
- WebSocket connections
- Order processing failures

## 11. Key Architecture Decisions

| Area | Proposed Direction | Status |
|---|---|---|
| Architecture | Microservices | Proposed |
| Backend | Java 21 + Spring Boot | Proposed |
| Frontend | React + TypeScript | Proposed |
| API entry point | Spring Cloud Gateway | Proposed |
| Synchronous communication | REST | Proposed |
| Asynchronous communication | Kafka | Proposed |
| Real-time communication | WebSocket | Proposed |
| Relational storage | PostgreSQL | Proposed |
| Caching | Redis | Proposed |
| Containerization | Docker | Proposed |
| Orchestration | Kubernetes | Proposed |
| Cloud | AWS | Proposed |
| Monitoring | Prometheus + Grafana | Proposed |
| CI/CD | GitHub Actions | Proposed |

## 12. Outstanding Design Decisions

The following questions must be discussed and resolved with the project team.

1. Will each microservice use a separate physical database or a separate schema?
2. Will authentication use a custom Identity Service or an external identity provider?
3. Will the order workflow use orchestration or choreography?
4. What is the expected GPS update interval?
5. How long will GPS history be retained?
6. Which notification provider will be used?
7. Which AWS services will be selected?
8. What are the backup retention, RPO, and RTO requirements?
9. What are the expected user and transaction volumes?
10. What are the production deployment and approval rules?
11. What is the infrastructure cost estimate?

These decisions should be documented before the relevant implementation details are finalized.

## 13. Peer Review Checklist

- [ ] Review HLD and confirm architectural consistency.
- [ ] Review all eight architecture diagrams in FP-DOC-003.
- [ ] Validate the eleven service boundaries.
- [ ] Review REST, Kafka, and WebSocket communication.
- [ ] Validate the order fulfilment workflow.
- [ ] Review tenant isolation and security.
- [ ] Review infrastructure and deployment assumptions.
- [ ] Discuss all outstanding design decisions.
- [ ] Record feedback and update documents.
- [ ] Obtain team approval.

## 14. Next Phase – Low-Level Design (LLD)

After System Design review and approval, the next phase will be Low-Level Design.

Proposed LLD activities:

1. Define detailed service-level architecture.
2. Define REST API contracts and request/response models.
3. Design database tables and relationships.
4. Define Kafka event schemas and topic configuration.
5. Design authentication and authorization flows.
6. Define error handling and validation standards.
7. Create service-level sequence diagrams.
8. Define testing strategy and acceptance criteria.
9. Prepare development tasks and implementation order.

Development should begin after the relevant LLD decisions are sufficiently clear.

## 15. Current Project Status

| Phase | Status |
|---|---|
| Business Requirements | Existing FP-DOC-001 |
| High-Level Design | Draft created |
| Architecture Diagrams | Draft completed |
| Microservices Design | Draft completed |
| Communication Design | Draft completed |
| Infrastructure Design | Draft completed |
| Consolidated System Design Summary | Draft completed |
| Peer Review | Pending |
| LLD | Not started |
| Development | Not started |

---

**End of Document – FP-DOC-007**

**FleetPulse – System Design Documentation Package**