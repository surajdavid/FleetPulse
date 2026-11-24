# FleetPulse – Task Allocation

**Status:** Proposed responsibility matrix; adapt to actual team size  
**Version:** 1.0

## 1. Purpose

This document allocates ownership by capability. It does not assume a specific number of developers. In a solo project, the same person may perform every role, but review gates and separation of concerns should still be followed.

## 2. Workstream ownership

| Workstream | Primary responsibility | Supporting responsibility |
|---|---|---|
| Product/BA | Scope, personas, acceptance criteria, prioritization | Resolve business-rule ambiguity |
| Architecture | Service boundaries, ADRs, security and integration contracts | Review cross-service changes |
| Backend | Service APIs, domain logic, persistence, events | Unit/integration tests |
| Frontend | React screens, API integration, UX states | Accessibility and browser tests |
| QA | Test scenarios, regression, integration and acceptance | Defect triage |
| DevOps | CI/CD, environments, secrets, deployment, backups | Observability and runbooks |
| Security | Threat review, authZ/authN, tenant isolation, secret handling | Dependency and configuration review |

## 3. Suggested implementation ownership by service

| Capability | Accountable workstream | Key deliverables |
|---|---|---|
| Identity/Tenant | Backend + Security | Authentication, membership, roles, tenant context |
| Customer | Backend + Frontend + QA | Customer APIs and management UI |
| Order/Shipment | Backend + Architecture + QA | Lifecycle APIs, state machine, events |
| Warehouse/Inventory | Backend + QA | Stock, reservations, concurrency |
| Payment simulation | Backend + Security + QA | Safe simulator, idempotency |
| Partner allocation | Backend + QA | Availability, assignment |
| Tracking | Backend + Frontend + QA | Location ingestion and tracking view |
| Notification | Backend + DevOps | Event consumers, provider adapters, DLQ |
| Reporting | Backend + Frontend + QA | Projections and dashboards |
| Platform/deployment | DevOps + Architecture | Environments, pipelines, telemetry |

## 4. Dependency coordination

- Architecture defines contracts before parallel service implementation.
- Backend owners publish API/event schemas for frontend and consumer teams.
- Database changes are owned by the service team that owns the data.
- QA prepares tests from acceptance criteria before feature completion.
- DevOps provides environment templates and pipeline gates early.
- Cross-service changes require review from affected service owners.

## 5. Solo-developer adaptation

Work in this order: define acceptance criteria → design contract → implement one vertical slice → test → document → PR/merge. Keep a visible task board with `Backlog`, `Ready`, `In Progress`, `In Review`, `Done` and record blockers explicitly.
