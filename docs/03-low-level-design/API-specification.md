# FleetPulse – API Specification

**Status:** Draft endpoint proposal – align final routes with the separate HLD service boundaries and approved security design  
**Revision:** 1.1

## 1. General contract

- Root path: `/api/v1`.
- Transport: HTTPS, JSON.
- Authentication: bearer access token.
- Tenant scope: resolved from trusted authenticated context.
- Time format: ISO-8601 UTC.
- IDs: UUID.
- Pagination: `page` (zero-based), `size` (bounded), `sort`.
- Error format: consistent problem response with `type`, `title`, `status`, `detail`, `instance`, `code`, `correlationId`, and optional field errors.
- Include `X-Correlation-ID`; accept a valid client-supplied ID or generate one.
- Use `Idempotency-Key` on operations that may be retried.

## 2. Proposed endpoint catalogue

| Capability | Method and path | Purpose |
|---|---|---|
| Authentication | `POST /api/v1/auth/login` | Authenticate (only if local auth is selected) |
| Tenant | `POST /api/v1/tenants` | Create tenant / onboarding |
| Tenant | `GET /api/v1/tenants/{tenantId}` | Read authorized tenant |
| Users | `POST /api/v1/tenants/{tenantId}/members` | Invite/add member |
| Customers | `POST /api/v1/customers` | Create customer |
| Customers | `GET /api/v1/customers` | Search/list customers |
| Customers | `GET /api/v1/customers/{customerId}` | Get customer |
| Customers | `PATCH /api/v1/customers/{customerId}` | Update customer |
| Orders | `POST /api/v1/orders` | Create order |
| Orders | `GET /api/v1/orders/{orderId}` | Get order |
| Shipments | `GET /api/v1/shipments` | Search shipments |
| Shipments | `GET /api/v1/shipments/{shipmentId}` | Get shipment |
| Shipments | `POST /api/v1/shipments/{shipmentId}/cancel` | Request cancellation |
| Warehouse | `POST /api/v1/warehouses` | Create warehouse |
| Inventory | `GET /api/v1/warehouses/{warehouseId}/inventory` | Read inventory |
| Inventory | `POST /api/v1/inventory/reservations` | Request reservation |
| Payment | `POST /api/v1/payments` | Simulate payment attempt |
| Payment | `GET /api/v1/payments/{paymentId}` | Get payment status |
| Delivery | `GET /api/v1/delivery-partners/available` | List eligible delivery partners |
| Delivery | `POST /api/v1/shipments/{shipmentId}/assignments` | Request partner assignment |
| Delivery | `POST /api/v1/shipments/{shipmentId}/proof-of-delivery` | Submit proof-of-delivery reference (storage and validation contract TBD) |
| Tracking | `POST /api/v1/tracking/location-updates` | Submit GPS update |
| Tracking | `GET /api/v1/shipments/{shipmentId}/tracking` | Get tracking timeline/latest position |
| Notifications | `GET /api/v1/notifications` | List authorized user notifications |
| Support | `POST /api/v1/support/tickets` | Create customer support ticket |
| Support | `GET /api/v1/support/tickets/{ticketId}` | Read authorized support ticket |
| Reporting | `GET /api/v1/reports/operations` | Operational summary |

These paths are illustrative candidates, not approved API contracts. The HLD names separate Identity, Tenant, Customer, Order, Payment, Inventory, Delivery, Tracking, Notification, Support and Reporting services. Final route ownership, authorization and endpoint coverage must be approved in OpenAPI. The authentication endpoint is conditional on the open choice between custom Spring Security and an external identity provider.

## 3. Example: create customer

Request:
```json
{
  "customerCode": "CUST-1001",
  "name": "Example Customer",
  "email": "customer@example.com",
  "phone": "+10000000000"
}
```

Response `201 Created`:
```json
{
  "id": "uuid",
  "customerCode": "CUST-1001",
  "name": "Example Customer",
  "email": "customer@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-01-01T10:00:00Z"
}
```

The tenant ID is intentionally not accepted as an authority-bearing field in this body; tenant scope is taken from authenticated context.

## 4. Example error response

```json
{
  "type": "https://api.fleetpulse.example/problems/validation",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields are invalid.",
  "instance": "/api/v1/customers",
  "code": "VALIDATION_ERROR",
  "correlationId": "uuid",
  "fieldErrors": [
    {"field": "email", "message": "must be a valid email address"}
  ]
}
```

## 5. Shipment state model

Do not treat the illustrative shipment states in this draft as an approved state machine. The BRD/SRS defines separate Order, Payment and Delivery state machines; use those source definitions as the baseline and reconcile their ownership with the HLD before publishing final API enums. Not every transition is valid. Record state changes and approve cancellation/refund/restock compensation rules before enabling those operations.

## 6. Security requirements

- Enforce role and tenant authorization server-side.
- Return `404` rather than disclose existence where resource visibility policy requires it.
- Validate body size, query limits, enum values and coordinates.
- Never return internal entity objects directly; use response DTOs.
- Never expose stack traces or secrets in errors.
- Document scopes/roles and examples in OpenAPI.
