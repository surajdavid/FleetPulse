# FleetPulse – Coding Standards

**Status:** Proposed baseline  
**Version:** 1.0

## 1. General

- Prefer readable, explicit code over clever abstractions.
- Keep methods focused and classes cohesive.
- Avoid speculative frameworks and unnecessary shared libraries.
- Use meaningful names; avoid unexplained abbreviations.
- Do not commit secrets, local configuration containing credentials, build output or generated artifacts.

## 2. Java backend

- Java 21 language/runtime baseline.
- Use the framework and dependency versions approved in the HLD/build files; do not upgrade dependencies casually.
- Package by business feature/service boundary, not one giant global controller/service/repository package.
- Use constructor injection; avoid field injection.
- Keep controllers thin; place use-case logic in application/service layer.
- Use request and response DTOs; do not expose persistence entities.
- Validate incoming requests at the boundary and enforce domain invariants in the domain/application layer.
- Use `BigDecimal` for monetary values; never `float`/`double`.
- Use `Instant`/UTC for event and audit timestamps.
- Use enums/value objects for constrained domain concepts.
- Use transactions at service use-case boundaries.
- Handle optimistic locking and idempotency explicitly.
- Never swallow exceptions; map known failures to documented API errors.
- Avoid logging tokens, passwords, secrets, payment credentials or full sensitive payloads.

## 3. REST and DTOs

- Follow `/api/v1` conventions and consistent status codes.
- Use validation annotations and clear field-level messages.
- Keep DTO names explicit, e.g. `CreateCustomerRequest`, `CustomerResponse`.
- Do not accept tenant identity as trusted input from a normal user request.
- Add pagination and sorting limits to list endpoints.
- Document examples and authorization requirements in OpenAPI.

## 4. Frontend

- Use reusable components with clear responsibility.
- Keep API calls in a dedicated client/service layer.
- Centralize authentication/session handling and API error mapping.
- Do not rely on frontend role checks for authorization; backend is authoritative.
- Handle loading, empty, validation, error and success states.
- Avoid storing secrets in browser storage; follow the approved token/session design.
- Keep accessible labels, keyboard navigation and responsive layouts.

## 5. Database and messaging

- Use versioned migrations; never rely on manual production schema edits.
- All tenant-owned queries must be tenant-scoped.
- Use outbox/inbox or equivalent reliable event processing.
- Event consumers must be idempotent.
- Do not share tables across services.
- Keep event payloads backward-compatible and versioned.

## 6. Formatting and quality gates

Configure formatter, static analysis and lint checks in the repository. Run the same checks locally and in CI. Pin tool/plugin versions. The exact tools (for example, Spotless/Checkstyle and ESLint/Prettier) should match the existing project configuration rather than creating duplicate formatters.

## 7. Naming

- Java types: `PascalCase`; methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Database tables/columns: choose one convention (recommended `snake_case`) and apply consistently.
- JSON properties: choose and document one convention (recommended `camelCase`).
- Events: past-tense domain names, e.g. `ShipmentCreated`.
