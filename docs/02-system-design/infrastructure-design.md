# FleetPulse – Infrastructure Design

**Status:** Draft – aligned to BRD/SRS v1.0 and HLD v1.0; detailed AWS mapping pending  
**Revision:** 1.1  
**Source documents:** FP-DOC-001, FP-DOC-002

## 1. Goals

Provide repeatable local development, automated verification, secure cloud deployment, service-level scaling, backups and operational visibility. AWS is the cloud direction in the source documents; the exact AWS service mapping and Kubernetes topology remain open.

## 2. Environments

| Environment | Purpose | Data |
|---|---|---|
| Local | Developer workflow and integration testing | Synthetic/test data |
| CI | Build, unit tests, static checks and selected integration tests | Ephemeral data |
| Development | Shared integration and feature validation | Synthetic data |
| Staging | Production-like acceptance and release verification | Masked/synthetic data |
| Production | Real customer operations | Protected tenant data |

Never copy production personal data into local or CI environments without an approved masking process.

## 3. Source-aligned logical components

- **Edge/API:** Spring Cloud API Gateway; external traffic over TLS.
- **Application:** Java 21, Spring Boot and Spring Cloud domain services.
- **Relational persistence:** PostgreSQL, with service-owned persistence models.
- **Messaging:** Apache Kafka for asynchronous business events.
- **Cache:** Redis for caching and selected low-latency state, including latest GPS location.
- **Real-time:** WebSocket/STOMP for authorized tracking/progress updates.
- **Object storage:** AWS S3 for documents and proof-of-delivery storage.
- **Observability:** Prometheus and Grafana; ELK-compatible centralized logs; metrics, logs and tracing/correlation.
- **CI/CD:** GitHub Actions.
- **Packaging/runtime:** Docker containers; Kubernetes is the stated deployment target, with exact topology to be designed.

## 4. Technology baseline and decision status

| Area | Source-document baseline | Status / caveat |
|---|---|---|
| Language | Java 21 | Proposed baseline in BRD/SRS and HLD |
| Backend | Spring Boot, Spring Cloud | Proposed baseline; do not mark framework as unconfirmed |
| Gateway | Spring Cloud Gateway | Proposed |
| Frontend | React + TypeScript | Proposed HLD baseline; web UI supports desktop and mobile-browser layouts |
| Database | PostgreSQL | Proposed baseline; physical database/schema topology open |
| Messaging | Apache Kafka | Proposed baseline |
| Cache | Redis | Proposed baseline for justified cache/latest-location use cases |
| Real-time | WebSocket / STOMP | Proposed baseline |
| Object storage | AWS S3 | Listed in BRD integration requirements; detailed configuration open |
| CI/CD | GitHub Actions | Proposed baseline in both source documents |
| Containers | Docker | Proposed |
| Orchestration/cloud | Kubernetes on AWS direction | Exact AWS services and Kubernetes topology open |
| Monitoring | Prometheus, Grafana, ELK-compatible logs | Proposed HLD stack |

## 5. Persistence topology clarification

Each microservice owns its persistence model and no service may access another service's tables directly. This is a logical ownership rule. The HLD explicitly leaves the physical choice open between separate PostgreSQL databases per service and separate schemas in a shared PostgreSQL cluster for the initial release. Database-per-tenant is not the v1 model; the source documents specify shared application infrastructure with logical tenant isolation.

## 6. Network and security zones

- Expose only the approved edge/API entry point publicly.
- Keep application services, Kafka, Redis and PostgreSQL in private network segments.
- Restrict database access to authorized service workloads.
- Use least-privilege identities, environment-specific credentials and encryption keys.
- Restrict and audit administrative access.
- Do not expose internal dependency details through public health endpoints.

## 7. Availability, backup and recovery

The BRD/SRS initial availability target is 99.5% monthly. This is a target to verify, not a guarantee from the proposed topology. Before production, define RPO, RTO, backup retention and recovery ownership. At minimum:
- Automated PostgreSQL backups and point-in-time recovery where supported.
- Periodic restore drills.
- Kafka durability, replication, retention and recovery procedures.
- Redis recovery/rebuild behavior for cache/latest-location data.
- Documented recovery steps and service ownership.

## 8. Capacity and scaling

The source documents give an initial planning range of 1,000–50,000 registered users across tenants. This is not a simultaneous-concurrency or throughput guarantee. Expected concurrent users, peak API request rates and GPS event rates remain HLD open questions. Use load tests and measured resource limits before sizing services, databases, Kafka partitions or Redis.

## 9. Open infrastructure decisions

- AWS service mapping for compute, PostgreSQL, Kafka, Redis, S3, networking and secrets.
- Kubernetes cluster/topology and operational ownership.
- Separate PostgreSQL databases versus schemas in a shared cluster.
- Region, backup retention, RPO/RTO and disaster-recovery design.
- Expected concurrent users, API rates and GPS update rates.
- Notification provider and its network/security requirements.
