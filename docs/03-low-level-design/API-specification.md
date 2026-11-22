# FP-DOC-009 – FleetPulse API Specification

**Version:** 1.0  
**Status:** Draft – Pending Team Review  
**Document Type:** Low-Level Design

## 1. Introduction

### 1.1 Purpose

This document defines the proposed REST API contracts for the FleetPulse microservices.

It describes API conventions, authentication requirements, request and response structures, endpoint responsibilities, and communication standards.

### 1.2 Scope

This document covers the APIs exposed by the following components:

1. API Gateway
2. Identity Service
3. Tenant Service
4. Customer Service
5. Order Service
6. Payment Service
7. Inventory Service
8. Delivery Service
9. Tracking Service
10. Notification Service
11. Support Service
12. Reporting Service

Kafka event contracts and WebSocket message structures will be documented separately.

## 2. API Architecture

### 2.1 API Gateway

The API Gateway acts as the central entry point for external client requests.

Responsibilities:

- Route requests to the appropriate microservice.
- Validate authentication tokens.
- Apply common security policies.
- Handle cross-cutting concerns such as request correlation.
- Apply rate limiting where configured.
- Forward authorized requests to the target service.

### 2.2 Proposed API Base URL

Local development:

`http://localhost:8080/api/v1`

The Gateway will expose versioned API routes.

### 2.3 Proposed Service Routing

| Service | Gateway Route |
|---|---|
| Identity | `/api/v1/auth/**`, `/api/v1/users/**` |
| Tenant | `/api/v1/tenants/**` |
| Customer | `/api/v1/customers/**` |
| Order | `/api/v1/orders/**` |
| Payment | `/api/v1/payments/**` |
| Inventory | `/api/v1/inventory/**` |
| Delivery | `/api/v1/deliveries/**`, `/api/v1/drivers/**` |
| Tracking | `/api/v1/tracking/**` |
| Notification | `/api/v1/notifications/**` |
| Support | `/api/v1/support/tickets/**` |
| Reporting | `/api/v1/reports/**` |

These routes are proposed and may be adjusted during implementation.

## 3. Common API Standards

### 3.1 HTTP Methods

| Method | Purpose |
|---|---|
| GET | Retrieve resources |
| POST | Create resources or initiate operations |
| PUT | Replace an existing resource |
| PATCH | Partially update a resource |
| DELETE | Delete a resource where permitted |

### 3.2 HTTP Status Codes

| Status | Meaning |
|---|---|
| 200 OK | Request successful |
| 201 Created | Resource created |
| 202 Accepted | Request accepted for asynchronous processing |
| 204 No Content | Request successful without response body |
| 400 Bad Request | Invalid request |
| 401 Unauthorized | Missing or invalid authentication |
| 403 Forbidden | Insufficient permissions |
| 404 Not Found | Resource not found |
| 409 Conflict | Business rule or duplicate resource conflict |
| 422 Unprocessable Entity | Request validation or business rule failure |
| 429 Too Many Requests | Rate limit exceeded |
| 500 Internal Server Error | Unexpected server error |
| 503 Service Unavailable | Service temporarily unavailable |

### 3.3 Request Headers

| Header | Purpose |
|---|---|
| `Authorization` | Bearer access token |
| `Content-Type` | Request content type |
| `Accept` | Expected response content type |
| `X-Correlation-ID` | Correlates requests across services |

Example:

`Authorization: Bearer <access-token>`

`X-Correlation-ID: 7f8a2c10-example`

The correlation ID should be propagated across downstream service calls and events where applicable.

### 3.4 Standard Success Response

Example:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": {
    "id": "b7c2c8d0-1234-4567-8901-123456789abc"
  },
  "timestamp": "2026-11-22T12:00:00Z"
}
```

The `data` object varies according to the endpoint.

### 3.5 Standard Error Response

Example:

```json
{
  "success": false,
  "message": "Validation failed",
  "errorCode": "VALIDATION_ERROR",
  "details": [
    {
      "field": "email",
      "message": "Email address is invalid"
    }
  ],
  "timestamp": "2026-11-22T12:00:00Z",
  "correlationId": "7f8a2c10-example"
}
```

Error responses should not expose internal stack traces, database details, secrets, or sensitive information.

### 3.6 Pagination

List endpoints should support pagination where applicable.

Proposed query parameters:

- `page` – zero-based page number.
- `size` – number of records per page.
- `sort` – field and direction.

Example:

`GET /api/v1/orders?page=0&size=20&sort=createdAt,desc`

Proposed response:

```json
{
  "success": true,
  "data": [],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

## 4. Authentication and Authorization Standards

1. Protected APIs must require a valid access token.
2. The Identity Service is responsible for authentication.
3. Authorization must be enforced using assigned roles and permissions.
4. Tenant context must be established from trusted authentication and authorization information.
5. Client-provided tenant IDs must never be trusted as the sole source of tenant identity.
6. Users must not access resources belonging to unauthorized tenants.
7. Public endpoints must be explicitly identified.
8. Service-to-service communication must use an approved authentication mechanism.
9. Sensitive information must never be exposed in API responses or logs.

The final identity provider and JWT implementation approach remain subject to team review.

## 5. API Versioning

- Public APIs will use URL-based versioning.
- Initial API version: `v1`.
- Breaking changes should introduce a new API version.
- Backward-compatible changes may be added within the existing version.
- Deprecated endpoints must follow an agreed deprecation policy.

## 6. General Design Rules

- Use UUIDs for proposed resource identifiers.
- Use ISO 8601 date-time formats.
- Use appropriate HTTP methods and status codes.
- Validate incoming request payloads.
- Apply tenant-level authorization to protected resources.
- Keep API controllers thin; business logic belongs in service classes.
- Do not expose database entities directly as API responses.
- Use request and response DTOs.
- Document APIs using OpenAPI/Swagger.
- Keep synchronous REST communication limited to operations that require an immediate response.

**Status:** Proposed API standards – Pending team review.