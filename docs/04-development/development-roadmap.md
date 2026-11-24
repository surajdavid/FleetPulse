# FleetPulse – Development Roadmap

**Status:** Proposed delivery plan – sequence and estimates to be reviewed  
**Version:** 1.0

## 1. Delivery principles

- Build in small vertical slices, each with API, persistence, tests, documentation and observability.
- Complete architecture decisions before introducing infrastructure complexity.
- Keep `main` releasable; merge only reviewed, CI-green changes.
- Treat tenant isolation and authorization as foundational acceptance criteria.
- Do not mark a feature complete based only on compilation.

## 2. Milestones

| Milestone | Scope | Exit criteria |
|---|---|---|
| M0 – Foundation | Java 21/Maven, backend startup/health, backend tests, React startup/build, lint/format, README, CI | **Completed per project checklist:** both PRs merged and CI passed |
| M1 – Architecture baseline | Review BRD/SRS and HLD; approve service boundaries, technology choices, tenancy and key workflows; complete remaining design docs | Documents reviewed, assumptions resolved or recorded, ADRs accepted |
| M2 – Platform foundation | Repository structure, API conventions, configuration, logging/tracing, error contract, migrations, security baseline | Local environment reproducible; health/readiness and CI checks pass |
| M3 – Identity and tenant | Tenant onboarding, users, roles, authentication, tenant context | Cross-tenant access tests pass; authorization documented |
| M4 – Customer management | Customer/address CRUD and search | Validation, pagination, tenant isolation and tests pass |
| M5 – Orders and shipments | Order creation, shipment lifecycle, state history | API contract and state-transition tests pass |
| M6 – Warehouse and inventory | Warehouse, stock and reservation workflow | Concurrency and reservation compensation tests pass |
| M7 – Payment simulation | Idempotent payment success/failure simulator | No sensitive payment data stored; retry and duplicate tests pass |
| M8 – Partner allocation | Partner availability and assignment | Concurrent assignment and failure handling tested |
| M9 – Tracking | GPS ingestion and tracking view | Coordinate validation, authorization, retention and load checks pass |
| M10 – Notifications | Event-driven notification delivery with retry/DLQ | Duplicate suppression and failure visibility pass |
| M11 – Reporting | Tenant-scoped operational projections | Reconciliation and eventual-consistency behavior documented |
| M12 – Release readiness | Security, performance, backup/restore, deployment, monitoring and runbooks | Release checklist approved; known risks accepted |

## 3. Definition of Done

A story is done only when:
- Acceptance criteria are met.
- Code follows coding standards and is peer-reviewed.
- Unit and relevant integration tests pass.
- Tenant authorization and negative tests are included where applicable.
- API/event/database changes are documented and backward compatibility considered.
- Logs/metrics/traces are adequate and contain no secrets.
- Migrations and rollback/forward-fix approach are reviewed.
- CI passes and changes are merged through the agreed workflow.

## 4. Prioritization

Prioritize security, tenant isolation, data correctness and core order lifecycle over dashboards or advanced route optimization. Keep external integrations simulated until core contracts are stable.
