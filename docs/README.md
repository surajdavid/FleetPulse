# FleetPulse – Remaining Engineering Documentation

This package contains the 17 documents requested beyond the already-prepared `FleetPulse-BRD-SRS-v1.0.md` and `HLD-v1.0.md`.

## Important status note

These documents are a **coherent first draft** based on the confirmed FleetPulse scope and completed foundation milestone. Any detail not present in the available project context is explicitly a proposal or open decision. Review and reconcile these drafts with the existing BRD/SRS and HLD before treating them as approved implementation contracts.

## Included documents

- `02-system-design/architecture-diagrams.md`
- `02-system-design/microservices-design.md`
- `02-system-design/communication-design.md`
- `02-system-design/infrastructure-design.md`
- `03-low-level-design/database-design.md`
- `03-low-level-design/API-specification.md`
- `03-low-level-design/event-catalog.md`
- `03-low-level-design/class-diagrams.md`
- `03-low-level-design/sequence-diagrams.md`
- `04-development/development-roadmap.md`
- `04-development/coding-standards.md`
- `04-development/git-workflow.md`
- `04-development/task-allocation.md`
- `05-testing/test-strategy.md`
- `06-deployment/deployment-strategy.md`
- `06-deployment/ci-cd-design.md`
- `06-deployment/monitoring-strategy.md`

## Suggested review order

1. `02-system-design/microservices-design.md`
2. `02-system-design/communication-design.md`
3. `03-low-level-design/database-design.md`
4. `03-low-level-design/API-specification.md`
5. `03-low-level-design/event-catalog.md`
6. `04-development/development-roadmap.md`
7. Remaining design, testing and deployment documents.

## Important decisions to confirm

- Exact backend framework and versions (Java 21 and Maven are confirmed; Spring Boot must match the existing HLD/repository).
- PostgreSQL or another relational database.
- Kafka vs RabbitMQ or another broker.
- Cloud provider and runtime/orchestration choice.
- Identity provider and authentication/session approach.
- Exact service boundaries and which services are deployable in the first release.
- Tenant isolation model, data retention, RPO/RTO and expected scale.

Do not overwrite the existing BRD/SRS or HLD with these drafts. Update the documents after review and record significant decisions as Architecture Decision Records (ADRs).
