# FleetPulse – Monitoring Strategy

**Status:** Proposed operational baseline  
**Version:** 1.0

## 1. Objectives

Detect user-impacting failures, explain cross-service workflows, protect tenant data and understand capacity before incidents occur.

## 2. Three pillars

### Logs
- Centralize structured application logs.
- Include timestamp, severity, service, environment, correlation ID and trace ID.
- Include tenant ID only where operationally necessary and safe; do not log sensitive payloads.
- Redact credentials, tokens, personal details and payment data.
- Define retention and access controls.

### Metrics
Recommended service metrics:
- Request count, error rate and latency percentiles.
- JVM heap, GC, thread pool and process health.
- Database connection pool, query latency and connection errors.
- Broker consumer lag, publish failures, retry and dead-letter counts.
- Order/shipment counts by state.
- Inventory reservation failures and conflicts.
- Payment simulator success/failure counts.
- GPS ingestion rate and rejected update count.
- Notification delivery success/failure and queue age.

Avoid high-cardinality labels such as raw customer IDs, shipment IDs or unrestricted tenant IDs.

### Traces
Use distributed tracing with trace/correlation propagation across gateway, REST calls and message headers. Trace sampling must balance troubleshooting value, cost and privacy.

## 3. Health endpoints

- Liveness: process is running and should not be restarted.
- Readiness: service can accept traffic; check only dependencies essential to safe operation.
- Startup probe where slow initialization requires it.
  Do not expose internal dependency details publicly.

## 4. Alerting

Create actionable alerts for:
- Sustained elevated 5xx rate or latency.
- Service unavailable / readiness failures.
- Database connection exhaustion or storage pressure.
- Broker outage, consumer lag or dead-letter growth.
- Repeated payment/assignment workflow failures.
- Notification backlog or provider failure spike.
- Backup failure and certificate/secret expiry.

Every alert should have severity, owner, runbook link and clear threshold rationale. Avoid alerting on a single transient failure.

## 5. Operational dashboards

1. Platform overview: availability, request rate, errors, latency.
2. Service health: JVM, DB pools, dependencies.
3. Logistics operations: orders and shipments by state, allocation and delivery outcomes.
4. Event pipeline: throughput, lag, retries, DLQ.
5. Tenant support: carefully permissioned tenant-level operational view.

## 6. SLOs and incident response

Define service-level indicators and objectives only after baseline traffic and business expectations are known. Candidate SLIs: successful API request ratio, p95 latency, event processing delay, and notification delivery delay.

Incident process: detect → acknowledge → assess impact → mitigate → communicate → recover → post-incident review. Record timeline, root cause, contributing factors, corrective actions and owners.

## 7. Privacy and access

Restrict observability access by role. Apply retention/deletion policy. Ensure dashboards and traces cannot be used to browse tenant data without authorization.
