# FleetPulse – Task Allocation

**Status:** Draft – aligned to BRD/SRS v1.0 and HLD v1.0; pending review responsibility matrix for the BRD/SRS assumption of two primary developers; confirm actual availability  
**Revision:** 1.1

## 1. Purpose

The BRD/SRS assumes two primary developers. The matrix below is a practical division of work for a two-person team, not a claim that each workstream is a separate person or department. Product decisions, QA, DevOps and security review may be shared responsibilities. Preserve peer review for high-risk changes.

## 2. Workstream ownership

| Workstream | Primary responsibility | Supporting responsibility |
|---|---|---|
| Developer A (suggested) | Backend foundations and service APIs, domain logic, persistence, Kafka integration | Backend unit/integration tests; API/event contracts |
| Developer B (suggested) | React + TypeScript UI, API integration, UX states | Frontend tests, accessibility and browser checks |
| Shared ownership | Requirements clarification, architecture/ADR decisions, tenant/security review, CI/CD, observability, acceptance testing and release readiness | Peer review; rotate ownership when dependencies require it |

## 3. Suggested implementation ownership by capability

| Capability | Suggested lead | Key deliverables |
|---|---|---|
| Identity and Tenant | Developer A, with shared security review | Authentication, users, roles, memberships, tenant context |
| Customer and Support | Developer A + Developer B for UI | Customer/address APIs, support ticket APIs and screens |
| Order and Payment | Developer A | Order lifecycle, simulated payment, idempotency and events |
| Inventory and Delivery | Developer A | Warehouses, stock/reservations, drivers, assignments and delivery status |
| Tracking and Notifications | Developer A + Developer B | GPS ingestion, Redis latest location, WebSocket UI, notification workflow |
| Reporting | Developer A + Developer B | Tenant-scoped projections and dashboards |
| Frontend platform | Developer B | React + TypeScript modules, API integration, responsive UX |
| Platform/deployment | Shared | GitHub Actions, Docker/Kubernetes path, AWS mapping, telemetry and runbooks |

## 4. Dependency coordination

- Architecture defines contracts before parallel service implementation.
- Backend owners publish API/event schemas for frontend and consumer teams.
- Database changes are owned by the service team that owns the data.
- QA prepares tests from acceptance criteria before feature completion.
- DevOps provides environment templates and pipeline gates early.
- Cross-service changes require review from affected service owners.

## 5. Coordination rules for a two-developer team

- Agree API, event and database contracts before parallel work begins.
- Avoid splitting work solely by microservice if one developer becomes blocked by another service.
- Integrate frequently through small PRs and keep the main branch buildable.
- Each developer reviews the other developer's high-risk changes where practical.
- Track shared tasks and decisions explicitly; do not assume a dedicated DevOps, QA or security team exists.
