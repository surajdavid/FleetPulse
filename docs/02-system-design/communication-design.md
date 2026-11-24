# FleetPulse – Communication Design

**Status:** Draft – aligned to BRD/SRS v1.0 and HLD v1.0; protocol details pending LLD  
**Revision:** 1.1  
**Source documents:** FP-DOC-001, FP-DOC-002

## 1. Communication patterns

| Interaction | Source-aligned pattern | Notes / unresolved detail |
|---|---|---|
| Browser to platform | HTTPS REST/JSON through Spring Cloud API Gateway | API base path and detailed endpoint contract are proposed below |
| Immediate service interaction | Synchronous REST | Apply timeouts, authentication, correlation and safe retry rules |
| Business lifecycle events | Apache Kafka | Versioned event envelope, idempotent consumers and outbox where justified |
| GPS update ingestion | Delivery partner client / simulator to Gateway and Tracking Service | Exact ingestion protocol and update interval are LLD open questions; do not present HTTPS as approved final protocol |
| Real-time tracking/progress | WebSocket / STOMP | Authorized customer and operations clients only |
| Document/proof-of-delivery storage | AWS S3 integration | Exact object lifecycle/access design remains for LLD/infrastructure design |
| Notifications | Notification Service plus email provider | Provider selection is explicitly open |

## 2. REST API conventions (proposed contract)

- Versioned base path: `/api/v1`.
- HTTPS, JSON and UTF-8.
- Use resource-oriented paths and standard HTTP semantics.
- Typical responses: `201 Created`, `200 OK`, `204 No Content`; use `400`, `401`, `403`, `404`, `409` and `500` consistently. Adopt `422` only if the API standard explicitly defines it.
- Use bounded pagination and stable sorting for collection endpoints.
- Use UTC timestamps in ISO-8601 format.
- Use an idempotency key for retryable operations where duplicate execution would be harmful, especially payment, order and assignment commands.
- Propagate a correlation ID across gateway, REST calls and Kafka messages.
- Publish and maintain the API contract using OpenAPI.

## 3. Authentication and tenant context

- Use TLS for external traffic.
- The HLD proposes Spring Security with OAuth2/JWT-compatible resource-server behavior. The exact authentication implementation and external identity-provider choice remain open.
- Validate token signature, issuer, audience, expiry and applicable roles/scopes according to the selected identity design.
- Resolve tenant context from verified claims or another trusted server-side context; never trust an arbitrary tenant ID in a request body/header.
- Enforce tenant ownership in each service and data-access path, not only in the frontend or gateway.
- Authenticate service-to-service communication. The specific workload identity mechanism is an implementation decision.

## 4. Kafka event envelope

The following is a proposed envelope aligned to HLD requirements, not a finalized schema:

```json
{
  "eventId": "uuid",
  "eventType": "ShipmentCreated",
  "eventVersion": 1,
  "occurredAt": "2026-01-01T10:00:00Z",
  "producer": "order-service",
  "tenantId": "tenant-uuid",
  "aggregateType": "Shipment",
  "aggregateId": "shipment-uuid",
  "correlationId": "correlation-uuid",
  "causationId": null,
  "payload": {}
}
```

Include event ID, event type, schema version, timestamp, tenant ID and correlation context. Avoid secrets, payment credentials and unnecessary personal data. Event names, payloads, topic names, partition keys and retention must be agreed in the event catalog before implementation.

## 5. Reliability and delivery semantics

- Set bounded connect/read/request timeouts for network calls.
- Retry only transient failures with bounded exponential backoff and jitter; do not retry validation or authorization failures.
- Use circuit breakers/bulkheads where justified by dependency risk.
- Use retry and dead-letter handling for failed Kafka consumers; monitor consumer lag and DLQ growth.
- Consumers must be idempotent and tolerate duplicate events.
- Use the transactional outbox for critical changes that must reliably publish an event.
- Define a controlled, audited replay process.

## 6. Cross-service consistency

Each service uses local ACID transactions. Cross-service workflows are eventually consistent. The HLD describes order fulfilment across inventory reservation, simulated payment, order readiness, delivery assignment and notifications. Intermediate states and failures must be visible to clients. Saga ownership, exact step ordering and compensation behavior remain open design decisions.

## 7. Security and privacy

- Encrypt traffic in transit and protect persisted data at rest.
- Store secrets outside source control and container images.
- Redact tokens, passwords, payment data and unnecessary personal information from logs/events.
- Apply gateway rate limits and request-size limits.
- Validate external input and authorize WebSocket subscriptions by tenant, role and resource ownership.
