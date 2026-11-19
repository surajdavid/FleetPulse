# FLEETPULSE
## Business Requirements Document (BRD) & Software Requirements Specification (SRS)

**Document ID:** FP-DOC-001  
**Version:** 1.0  
**Status:** Draft – Pending Team Approval  
**Product Type:** Multi-Tenant Logistics SaaS Platform  
**Prepared for:** FleetPulse Development Team  
**Last Updated:** November 2026

---

# PART A – BUSINESS REQUIREMENTS DOCUMENT (BRD)

## 1. Executive Summary

FleetPulse is a multi-tenant logistics and delivery management SaaS platform designed to help logistics companies manage their complete delivery operations through a centralized digital system.

The platform will connect customers, logistics administrators, warehouse managers, delivery partners and customer support agents.

It will support the complete logistics lifecycle, including:

- Customer registration and management.
- Shipment and order creation.
- Warehouse and inventory management.
- Payment simulation.
- Delivery partner allocation.
- Real-time GPS tracking.
- Delivery status management.
- Customer notifications.
- Operational monitoring and reporting.

FleetPulse will follow a production-oriented software architecture with independent microservices, secure communication, event-driven processing, automated testing and cloud-ready deployment.

## 2. Business Vision

To build a scalable, secure and reliable logistics management platform that simplifies delivery operations, improves shipment visibility and enables logistics companies to manage their businesses efficiently.

## 3. Business Objectives

| ID | Objective |
|---|---|
| BO-01 | Provide a centralized logistics management platform. |
| BO-02 | Support multiple independent logistics companies. |
| BO-03 | Digitize the complete order-to-delivery lifecycle. |
| BO-04 | Provide real-time delivery tracking. |
| BO-05 | Improve warehouse and inventory visibility. |
| BO-06 | Automate delivery allocation and notifications. |
| BO-07 | Provide operational dashboards and reporting. |
| BO-08 | Maintain secure tenant-level data isolation. |
| BO-09 | Support independent scaling of backend services. |
| BO-10 | Enable reliable deployment and monitoring. |

## 4. Business Model

FleetPulse will operate as a multi-tenant SaaS platform.

Each logistics company will be registered as an independent tenant.

A tenant will have its own:
- Administrators.
- Customers.
- Delivery partners.
- Warehouses.
- Inventory records.
- Orders and shipments.
- Operational reports.

A platform-level administrator will manage the overall SaaS environment.

### Tenant isolation principles

1. Every tenant-owned business record must be associated with a tenant.
2. Users must not access another tenant's business data.
3. Tenant context must be established through authenticated and validated identity.
4. Tenant isolation must be enforced in backend services, not merely in the frontend.
5. Cross-tenant access must be explicitly authorized and audited.
6. Platform-level administrative access must be restricted and monitored.

**Initial architecture assumption:** Shared application infrastructure with logical tenant isolation in the database. Database-per-tenant architecture is not part of the initial release.

## 5. Stakeholders and User Roles

### 5.1 Platform Administrator

Responsible for the overall SaaS platform.

Responsibilities:
- Register and manage logistics tenants.
- Activate or suspend tenant accounts.
- Monitor platform health.
- Manage platform-level configurations.
- Review security and audit information.

### 5.2 Tenant Administrator

Responsible for managing a logistics company's operations.

Responsibilities:
- Manage employees and permissions.
- Manage customers and delivery partners.
- Manage warehouses.
- Monitor orders and shipments.
- Assign delivery partners.
- View operational reports.

### 5.3 Customer

Responsibilities:
- Register and manage personal information.
- Manage delivery addresses.
- Create shipment requests.
- View shipment history.
- Initiate eligible cancellations.
- Simulate payments.
- Track shipments.
- Submit support requests.

### 5.4 Delivery Partner

Responsibilities:
- Access assigned deliveries.
- Accept or reject delivery assignments.
- View pickup and delivery addresses.
- Update delivery status.
- Submit GPS location updates.
- Record delivery completion.
- Upload proof of delivery where enabled.

### 5.5 Warehouse Manager

Responsibilities:
- Manage warehouse information.
- Manage inventory.
- Receive incoming shipments.
- Prepare outgoing shipments.
- Update package dispatch status.
- Monitor stock movements.

### 5.6 Support Agent

Responsibilities:
- View authorized customer and shipment information.
- Manage customer support tickets.
- Investigate delivery issues.
- Record support interactions.
- Escalate unresolved issues.

Support agents must not perform unrestricted financial or administrative operations.

## 6. Business Scope

### 6.1 In Scope

The initial complete product scope includes:

1. Tenant management.
2. User identity and access management.
3. Customer management.
4. Order and shipment management.
5. Payment simulation.
6. Warehouse management.
7. Inventory management.
8. Delivery partner management.
9. Delivery assignment.
10. Live GPS tracking.
11. Notification management.
12. Customer support management.
13. Administrative dashboards.
14. Reporting and operational analytics.
15. Security and audit logging.
16. Automated testing.
17. Containerization and deployment.
18. Monitoring and observability.

### 6.2 Out of Scope for Version 1

The following features are excluded from the initial release:

- Actual financial settlement and production payment processing.
- Real banking integrations.
- Automated AI-based route optimization.
- Predictive delivery time estimation using machine learning.
- International customs management.
- Advanced multi-currency accounting.
- Native Android and iOS applications.
- External third-party carrier integrations.
- Automated billing and subscription collection.
- Advanced warehouse robotics integration.

These may be considered in future versions.

## 7. Business Workflow

### 7.1 Standard Shipment Lifecycle

1. Customer registers or logs in.
2. Customer creates a shipment request.
3. System validates shipment information.
4. System checks relevant inventory and warehouse availability.
5. Customer initiates simulated payment.
6. Payment service processes the simulation.
7. Order is confirmed.
8. Warehouse prepares the shipment.
9. Shipment is dispatched.
10. Administrator or allocation logic assigns a delivery partner.
11. Delivery partner accepts the assignment.
12. Delivery partner picks up the shipment.
13. GPS tracking becomes available.
14. Delivery partner updates shipment progress.
15. Shipment is delivered.
16. Customer receives a completion notification.
17. Shipment history is retained.

### 7.2 Exception Workflows

The system must also handle:

- Payment failure.
- Duplicate payment requests.
- Inventory unavailability.
- Delivery assignment rejection.
- Driver unavailability.
- Delivery cancellation.
- Failed delivery attempts.
- GPS signal interruption.
- Notification delivery failure.
- Service and database failures.

## 8. Business Success Criteria

The following are initial measurable targets to validate during performance and acceptance testing.

| Metric | Initial target |
|---|---|
| API availability | 99.5% monthly |
| Standard API response time | P95 below 500 ms |
| GPS update visibility | P95 below 5 seconds under normal test conditions |
| Tenant data isolation | 100% of defined isolation tests pass |
| Critical business workflows | 100% of acceptance tests pass |
| Automated build | Successful CI pipeline |
| Critical security findings | Zero unresolved critical findings at release |

Targets are subject to refinement after workload modelling and infrastructure testing.

## 9. Business Assumptions and Constraints

### Assumptions

- Each logistics company is represented by one tenant.
- Customers and delivery partners belong to a tenant for business operations.
- Customers may have multiple delivery addresses.
- Payments will initially be simulated.
- GPS coordinates will be submitted by a delivery partner's client application or a simulator.
- A web-based frontend will be used initially.
- The first release will support a limited geographic operating area.

### Constraints

- Two primary developers will develop the initial system.
- Java and Spring Boot will be the primary backend technologies.
- React will be used for the web frontend.
- Infrastructure costs should remain manageable.
- Services must be independently testable.
- Security and data isolation are mandatory design requirements.

---

# PART B – SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

## 10. Introduction

### 10.1 Purpose

This document specifies the functional and non-functional requirements for FleetPulse Version 1.

It will serve as the reference for:

- System architecture.
- Database design.
- API development.
- Implementation planning.
- Testing.
- Acceptance testing.
- Deployment.

### 10.2 Intended Audience

- Backend developers.
- Frontend developers.
- Software architects.
- QA engineers.
- DevOps engineers.
- Project stakeholders.

### 10.3 Product Perspective

FleetPulse is a distributed SaaS application consisting of independently deployable backend services, a web frontend and supporting infrastructure.

### 10.4 Product Users

- Platform Administrator.
- Tenant Administrator.
- Customer.
- Delivery Partner.
- Warehouse Manager.
- Support Agent.

## 11. Proposed Technology Stack

| Layer | Technology |
|---|---|
| Programming language | Java 21 |
| Backend framework | Spring Boot |
| Microservices | Spring Cloud |
| API Gateway | Spring Cloud Gateway |
| Security | Spring Security, OAuth2/JWT |
| Relational database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Database migrations | Flyway |
| Messaging | Apache Kafka |
| Caching | Redis |
| Real-time communication | WebSocket |
| Frontend | React |
| API documentation | OpenAPI / Swagger |
| Unit testing | JUnit 5, Mockito |
| Integration testing | Testcontainers |
| Containerization | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Monitoring | Prometheus and Grafana |
| Logging | ELK-compatible centralized logging |
| Cloud | AWS |

Exact framework and dependency versions will be frozen during HLD and project initialization.

## 12. Proposed Microservices

| Service | Responsibility |
|---|---|
| API Gateway | Centralized request routing and edge security |
| Identity Service | Authentication, users and authorization |
| Tenant Service | Tenant registration and lifecycle |
| Customer Service | Customer profiles and addresses |
| Order Service | Orders and shipment lifecycle |
| Payment Service | Simulated payment processing |
| Inventory Service | Warehouse stock and reservations |
| Delivery Service | Driver allocation and delivery operations |
| Tracking Service | GPS location and tracking history |
| Notification Service | Email and notification delivery |
| Support Service | Customer support tickets |
| Reporting Service | Operational analytics |

These are proposed logical boundaries. Their final deployment boundaries and ownership will be approved in the HLD.

## 13. Functional Requirements

### 13.1 Identity and Access Management

**FR-IAM-001:** The system shall allow authorized users to register and authenticate.

**FR-IAM-002:** The system shall support role-based access control.

**FR-IAM-003:** The system shall support secure token-based authentication.

**FR-IAM-004:** The system shall support token expiration and refresh according to the selected authentication design.

**FR-IAM-005:** The system shall prevent unauthorized access to protected resources.

**FR-IAM-006:** The system shall maintain an audit record of important authentication and authorization events.

**FR-IAM-007:** The system shall enforce tenant-level access restrictions.

### 13.2 Tenant Management

**FR-TEN-001:** Platform administrators shall be able to register logistics companies.

**FR-TEN-002:** Platform administrators shall be able to activate or suspend tenants.

**FR-TEN-003:** Each tenant shall have a unique identifier.

**FR-TEN-004:** Each tenant shall have independently managed users and business records.

**FR-TEN-005:** Tenant suspension shall prevent normal business operations according to the configured suspension policy.

### 13.3 Customer Management

**FR-CUS-001:** Customers shall be able to register and authenticate.

**FR-CUS-002:** Customers shall be able to manage their profiles.

**FR-CUS-003:** Customers shall be able to add, edit and delete eligible delivery addresses.

**FR-CUS-004:** Customers shall be able to view their shipment history.

**FR-CUS-005:** Customers shall only access their own customer information.

### 13.4 Order Management

**FR-ORD-001:** Customers shall be able to create shipment requests.

**FR-ORD-002:** Each order shall receive a unique reference number.

**FR-ORD-003:** The system shall validate shipment details before confirmation.

**FR-ORD-004:** The system shall maintain an order status lifecycle.

**FR-ORD-005:** Customers shall be able to view shipment details.

**FR-ORD-006:** Customers shall be able to request cancellation when cancellation rules permit.

**FR-ORD-007:** Administrators shall be able to view and manage authorized tenant orders.

**FR-ORD-008:** The system shall maintain shipment status history.

**FR-ORD-009:** Duplicate order submissions shall be controlled using idempotency mechanisms.

### 13.5 Payment Management

**FR-PAY-001:** Customers shall be able to initiate simulated payments.

**FR-PAY-002:** The system shall support successful and failed simulated payment outcomes.

**FR-PAY-003:** Each payment shall have a unique transaction reference.

**FR-PAY-004:** The system shall maintain payment status.

**FR-PAY-005:** The system shall prevent duplicate payment processing.

**FR-PAY-006:** Payment results shall be communicated to the Order Service.

**FR-PAY-007:** The system shall maintain payment audit records.

**FR-PAY-008:** The system shall support simulated refund workflows where applicable.

No actual financial transactions shall occur in Version 1.

### 13.6 Inventory and Warehouse Management

**FR-INV-001:** Authorized users shall be able to create and manage warehouse records.

**FR-INV-002:** Warehouse managers shall be able to view inventory.

**FR-INV-003:** The system shall maintain stock quantities.

**FR-INV-004:** The system shall support inventory reservation.

**FR-INV-005:** The system shall prevent invalid stock deductions.

**FR-INV-006:** The system shall maintain stock movement history.

**FR-INV-007:** The system shall support incoming and outgoing shipment operations.

**FR-INV-008:** The system shall prevent unauthorized cross-tenant inventory access.

### 13.7 Delivery Management

**FR-DEL-001:** Administrators shall be able to assign delivery partners.

**FR-DEL-002:** Delivery partners shall be able to view their assigned deliveries.

**FR-DEL-003:** Delivery partners shall be able to accept or reject assignments.

**FR-DEL-004:** The system shall maintain delivery status.

**FR-DEL-005:** Delivery partners shall be able to update pickup and delivery progress.

**FR-DEL-006:** The system shall record failed delivery attempts.

**FR-DEL-007:** The system shall support proof of delivery.

**FR-DEL-008:** The system shall validate delivery status transitions.

### 13.8 GPS Tracking

**FR-TRK-001:** Authorized delivery partners shall be able to submit GPS coordinates.

**FR-TRK-002:** The system shall associate location updates with the appropriate delivery and tenant.

**FR-TRK-003:** Customers shall be able to view authorized live delivery locations.

**FR-TRK-004:** The system shall record location timestamps.

**FR-TRK-005:** The system shall support real-time location updates using WebSocket communication.

**FR-TRK-006:** The system shall handle GPS interruptions and stale location information.

**FR-TRK-007:** The system shall validate location update authorization.

**FR-TRK-008:** The system shall apply configurable location retention policies.

### 13.9 Notification Management

**FR-NOT-001:** The system shall generate notifications for important shipment events.

**FR-NOT-002:** The system shall notify customers about order confirmation.

**FR-NOT-003:** The system shall notify customers about payment outcomes.

**FR-NOT-004:** The system shall notify customers about delivery progress.

**FR-NOT-005:** The system shall record notification delivery attempts.

**FR-NOT-006:** Failed notification attempts shall support retry according to configured policies.

### 13.10 Customer Support

**FR-SUP-001:** Customers shall be able to create support tickets.

**FR-SUP-002:** Support agents shall be able to view assigned tickets.

**FR-SUP-003:** Support agents shall be able to update ticket status.

**FR-SUP-004:** The system shall maintain ticket history.

**FR-SUP-005:** Support agents shall only access authorized customer and shipment information.

### 13.11 Reporting and Administration

**FR-REP-001:** Tenant administrators shall be able to view shipment summaries.

**FR-REP-002:** Administrators shall be able to view delivery performance.

**FR-REP-003:** Administrators shall be able to view payment simulation summaries.

**FR-REP-004:** Administrators shall be able to view inventory summaries.

**FR-REP-005:** Platform administrators shall be able to monitor tenant status.

**FR-REP-006:** Reporting information shall respect tenant access restrictions.

## 14. Core Business State Machines

### 14.1 Order Lifecycle

Proposed order states:

CREATED → PAYMENT_PENDING → CONFIRMED → PROCESSING → READY_FOR_DISPATCH → DISPATCHED → IN_TRANSIT → DELIVERED

Exception states:

PAYMENT_FAILED, CANCELLED, DELIVERY_FAILED, RETURN_INITIATED, RETURNED.

Every transition must be validated. The system must not allow arbitrary status changes.

### 14.2 Payment Lifecycle

CREATED → PENDING → SUCCESSFUL

Alternative outcomes:

FAILED, CANCELLED, REFUND_PENDING, REFUNDED.

### 14.3 Delivery Lifecycle

ASSIGNED → ACCEPTED → PICKED_UP → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED

Alternative outcomes:

REJECTED, FAILED, RESCHEDULED, RETURNED.

Detailed transition rules will be defined in the LLD.

## 15. Non-Functional Requirements

### 15.1 Performance

**NFR-PER-001:** Standard APIs should target P95 response times below 500 milliseconds under the agreed normal test workload.

**NFR-PER-002:** GPS updates should target P95 visibility below five seconds under normal network conditions.

**NFR-PER-003:** The platform shall support independently scalable services.

**NFR-PER-004:** Database queries shall be optimized and monitored.

### 15.2 Scalability

**NFR-SCL-001:** The system shall support an initial planning range of 1,000–50,000 registered users across tenants.

**NFR-SCL-002:** The architecture shall support horizontal scaling of stateless application services.

**NFR-SCL-003:** Kafka shall support asynchronous communication between selected services.

**NFR-SCL-004:** Redis may be used for caching and other justified low-latency workloads.

The registered-user target is not equivalent to simultaneous active users. Concurrency and throughput targets will be defined during capacity planning.

### 15.3 Availability and Reliability

**NFR-REL-001:** The initial availability target shall be 99.5% monthly.

**NFR-REL-002:** Services shall expose health-check endpoints.

**NFR-REL-003:** The system shall implement suitable timeout and retry mechanisms.

**NFR-REL-004:** Critical event processing shall support duplicate detection and idempotency.

**NFR-REL-005:** The architecture shall support recovery from temporary downstream service failures.

**NFR-REL-006:** Data backup and recovery procedures shall be documented.

### 15.4 Security

**NFR-SEC-001:** All production network communication shall use encrypted transport.

**NFR-SEC-002:** Authentication and authorization shall be enforced server-side.

**NFR-SEC-003:** Passwords shall be stored using secure password hashing.

**NFR-SEC-004:** Sensitive credentials shall not be committed to source control.

**NFR-SEC-005:** Tenant isolation shall be enforced at all relevant data-access boundaries.

**NFR-SEC-006:** APIs shall validate and sanitize incoming input.

**NFR-SEC-007:** Security-sensitive operations shall be audited.

**NFR-SEC-008:** GPS location data shall only be accessible to authorized users.

**NFR-SEC-009:** The system shall follow least-privilege access principles.

### 15.5 Maintainability

**NFR-MNT-001:** Services shall follow consistent coding conventions.

**NFR-MNT-002:** Business logic shall be separated from controllers and persistence layers.

**NFR-MNT-003:** APIs shall be documented using OpenAPI.

**NFR-MNT-004:** Each service shall include automated tests.

**NFR-MNT-005:** Database schema changes shall be version-controlled.

### 15.6 Observability

**NFR-OBS-001:** Services shall produce structured logs.

**NFR-OBS-002:** Services shall expose application metrics.

**NFR-OBS-003:** Distributed requests shall be traceable using correlation and trace identifiers.

**NFR-OBS-004:** Monitoring dashboards shall display service health and important performance indicators.

**NFR-OBS-005:** Critical operational failures shall generate alerts.

### 15.7 Usability

**NFR-USA-001:** The web interface shall support desktop and mobile browser layouts.

**NFR-USA-002:** Error messages shall be understandable to end users.

**NFR-USA-003:** User interfaces shall provide appropriate loading, success and failure states.

## 16. External Interface Requirements

### 16.1 User Interface

The frontend shall provide:

- Customer portal.
- Tenant administration portal.
- Delivery partner interface.
- Warehouse management interface.
- Support agent interface.
- Platform administration interface.

### 16.2 API Interface

Backend services shall expose versioned REST APIs.

Example:

`/api/v1/orders`

API responses shall use a consistent response and error format.

### 16.3 Messaging Interface

Kafka shall be used for asynchronous business event communication.

Proposed event names:

- OrderCreated
- PaymentSucceeded
- PaymentFailed
- InventoryReserved
- DeliveryAssigned
- DeliveryAccepted
- ShipmentDispatched
- LocationUpdated
- ShipmentDelivered

Event schemas and topic naming will be finalized in the HLD and LLD.

### 16.4 Real-Time Interface

WebSocket communication shall support authorized delivery location updates and shipment progress notifications.

## 17. Data Requirements

The system is expected to maintain the following major business entities:

- Tenant
- User
- Role
- Permission
- Customer
- CustomerAddress
- Warehouse
- InventoryItem
- StockMovement
- Order
- Shipment
- Payment
- Delivery
- DeliveryAssignment
- GPSLocation
- Notification
- SupportTicket
- AuditLog

### Data Management Principles

1. Each tenant-owned record shall have a tenant identifier.
2. Primary keys shall be globally unique where appropriate.
3. Foreign-key relationships shall be defined within service-owned data boundaries.
4. Database migrations shall be managed using Flyway.
5. Sensitive data shall be protected.
6. Data retention and deletion rules shall be documented.
7. Each microservice shall own its persistence model.

A complete ER diagram will be delivered as part of the database LLD.

## 18. Integration Requirements

The system shall support the following integrations:

| Integration | Purpose |
|---|---|
| Kafka | Asynchronous service communication |
| Redis | Caching and selected fast-access operations |
| PostgreSQL | Relational persistence |
| WebSocket | Real-time tracking |
| Simulated Payment Gateway | Payment workflow testing |
| Email provider | Customer notifications |
| AWS S3 | Document and proof-of-delivery storage |
| Prometheus | Metrics collection |
| Grafana | Operational monitoring |

External provider choices and production credentials will be determined during implementation.

## 19. Testing Requirements

The system shall include:

### Unit Testing
Test individual business logic components.

### Integration Testing
Validate database interactions and service integrations.

### API Testing
Validate endpoint behavior, authentication and error responses.

### Contract Testing
Verify event schemas and communication contracts between services.

### Security Testing
Validate authentication, authorization, tenant isolation and input handling.

### Performance Testing
Measure latency, throughput and resource consumption.

### End-to-End Testing
Validate complete business workflows.

### Resilience Testing
Validate behavior during service failures, network interruptions and message retries.

## 20. Deployment Requirements

The application shall support:

- Local development using Docker Compose.
- Independent service container builds.
- Automated builds and tests using GitHub Actions.
- Environment-specific configuration.
- Kubernetes deployment.
- Centralized logging and monitoring.
- Secure secret management.
- Database migration execution.
- Backup and recovery procedures.

## 21. Acceptance Criteria

Version 1 shall be considered ready for release when:

1. Multiple tenants can be registered and managed.
2. Tenant data isolation tests pass.
3. Users can authenticate and access authorized functionality.
4. Customers can create and track shipments.
5. Simulated payments can succeed and fail.
6. Inventory can be managed and reserved.
7. Delivery partners can accept and complete assignments.
8. GPS tracking works through the agreed client/simulator workflow.
9. Customers receive shipment notifications.
10. Support agents can manage support tickets.
11. Administrative dashboards display operational data.
12. Critical business workflows pass end-to-end tests.
13. Automated CI checks pass.
14. Monitoring and logging are operational.
15. Deployment and recovery documentation is available.
16. No unresolved critical security findings remain.

## 22. Risks and Mitigation

| Risk | Mitigation |
|---|---|
| Microservice complexity | Define clear service boundaries and contracts |
| Cross-tenant data leakage | Enforce tenant context at backend and persistence boundaries |
| Duplicate Kafka events | Implement idempotent consumers |
| Distributed transaction failures | Use saga and outbox patterns where justified |
| GPS interruptions | Handle stale updates and reconnection |
| Database inconsistency | Define ownership and consistency rules |
| Unauthorized access | Apply RBAC and least privilege |
| Deployment complexity | Start with Docker Compose and progress to Kubernetes |
| Scope expansion | Maintain a prioritized backlog |

## 23. Future Enhancements

Potential future versions may introduce:

- Real payment gateway integration.
- Intelligent route optimization.
- Estimated delivery time prediction.
- Automated delivery partner allocation.
- Advanced analytics.
- Subscription and tenant billing.
- Native mobile applications.
- External carrier integrations.
- Multi-region deployment.
- AI-powered operational assistance.

## 24. Document Approval

This specification is pending review and approval.

| Role | Name | Status |
|---|---|---|
| Developer 1 | TBD | Pending |
| Developer 2 | TBD | Pending |
| Product Owner | TBD | Pending |

---

**END OF FLEETPULSE BRD & SRS VERSION 1.0**

Document classification: Internal Project Documentation.