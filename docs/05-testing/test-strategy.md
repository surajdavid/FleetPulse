# FleetPulse – Test Strategy

**Status:** Draft – aligned to BRD/SRS v1.0 and HLD v1.0; test thresholds are acceptance targets, not proven results strategy  
**Revision:** 1.1

## 1. Objectives

The BRD/SRS defines initial targets of 99.5% monthly API availability, standard API P95 below 500 ms, and GPS update visibility P95 below five seconds under normal test conditions. Validate these targets in agreed test environments; do not describe them as measured production performance before evidence exists.

Verify functional correctness, tenant isolation, security, reliability, data consistency and acceptable performance across the platform. Tests must validate behavior, not merely code execution.

## 2. Test levels

| Level | Scope | Examples |
|---|---|---|
| Unit | Domain/application logic | State transitions, pricing, reservation rules |
| Repository/integration | Persistence and migrations | Tenant-scoped queries, constraints, optimistic locking |
| API/component | HTTP contract and validation | Status codes, DTO shape, auth failures |
| Messaging integration | Producer/consumer behavior | Outbox, duplicate events, retry, DLQ |
| End-to-end | Critical user journeys | Customer order through delivery |
| Security | AuthN/AuthZ and tenant isolation | Cross-tenant IDOR, role escalation, token validation |
| Performance | Throughput and latency | Order creation, GPS ingestion, dashboard reads |
| Resilience | Dependency and broker failures | Timeout, retry, restart, replay |
| Accessibility/UI | React behavior | Keyboard navigation, form errors, responsive layouts |

## 3. Critical scenarios

### Multi-tenancy
- Tenant A cannot read, update, delete or infer Tenant B records.
- Tenant ID spoofing in request body/header is rejected or ignored.
- Background event consumers preserve tenant scope.
- Reporting projections never mix tenants.
- Admin/support access is explicit, limited and audited.

### Order and shipment
- Valid order creation.
- Invalid customer, item quantity or address.
- Duplicate request with same idempotency key.
- Invalid and concurrent shipment state transitions.
- Cancellation and compensation behavior.

### Inventory
- Successful reservation.
- Insufficient stock.
- Concurrent reservations cannot oversell.
- Duplicate reservation event does not reserve twice.
- Reservation expiry/release behavior.

### Payment
- Simulated success and failure only; real financial settlement is out of scope for v1.
- Duplicate payment command is idempotent.
- Payment timeout/retry does not double-charge (or double-simulate).
- No card credentials or sensitive payment data are persisted/logged.

### Allocation and tracking
- Partner eligibility and concurrent assignment.
- Unauthorized partner cannot submit a location for another shipment.
- Invalid coordinates and stale timestamps are handled.
- Duplicate/out-of-order GPS updates are handled predictably.

### Notifications and reporting
- Retryable provider error and dead-letter behavior.
- Duplicate event does not send duplicate notification.
- Reporting projection catches up after replay.
- Eventual consistency is represented accurately.

## 4. Test data

Use synthetic tenants and users with different roles. Include at least two tenants in automated security tests. The planning range of 1,000–50,000 registered users is not a concurrency target; concurrency and peak request/GPS rates must be agreed before load-test sizing. Avoid real customer information. Reset ephemeral test data deterministically.

## 5. Automation and gates

CI should run formatting/lint, compile/build, unit tests and fast integration tests. PR gates should include relevant service contract tests. Nightly or scheduled pipelines may run broader end-to-end, security and performance suites.

## 6. Entry/exit criteria

Entry: acceptance criteria and testable contract exist; dependencies/environment available.  
Exit: all critical tests pass, 100% of tenant-isolation tests pass, no unresolved critical security finding remains for release, no unresolved blocker/high-severity defect remains, test evidence is recorded, and known limitations are accepted by the owner. These release gates reflect the BRD/SRS acceptance criteria.

## 7. Test reporting

Report pass/fail, skipped tests with reason, coverage trends, flaky tests, defect severity and environment. Coverage percentage is a signal, not the sole quality measure.
