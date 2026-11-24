# FleetPulse – Deployment Strategy

**Status:** Draft – aligned to BRD/SRS v1.0 and HLD v1.0; pending review AWS-oriented deployment strategy – detailed AWS/Kubernetes topology pending HLD open questions  
**Revision:** 1.1

## 1. Release principles

The source documents specify AWS as the cloud direction, Docker containers, Kubernetes as the deployment target and GitHub Actions for CI/CD. Exact AWS service mapping and cluster topology remain open; this document does not select specific AWS compute/database/messaging products.

- Build once and promote the same immutable artifact through environments.
- Use versioned container images and database migrations.
- Keep configuration external to images.
- Deploy backward-compatible changes before removing old contracts.
- Use health/readiness checks and automated rollback or forward-fix procedures.
- Never deploy unreviewed code directly to production.

## 2. Environments and promotion

1. Local developer environment.
2. CI ephemeral environment for automated tests.
3. Development environment for integration.
4. Staging for release candidate verification.
5. Production after approval.

Promotion requires successful tests, vulnerability/dependency checks, migration review and release notes.

## 3. Deployment approach

Use Docker images and the approved Kubernetes deployment target when the environment is established. Keep the local developer environment lightweight (Docker Compose is a local-development option, not a production topology decision).

For stateless services, use rolling deployment with readiness checks. Use blue/green or canary for higher-risk changes when the chosen platform supports it. Database migrations must follow expand-and-contract:
1. Add backward-compatible schema.
2. Deploy code compatible with old and new schema.
3. Backfill if required.
4. Switch reads/writes.
5. Remove obsolete schema in a later release.

## 4. Configuration and secrets

- Separate configuration per environment.
- Store credentials in a managed secrets store.
- Use workload identity/short-lived credentials where possible.
- Rotate secrets and document emergency revocation.
- Do not expose secrets in CI logs or container images.

## 5. Rollback and recovery

- Keep prior known-good image versions.
- Define service-specific rollback instructions.
- Do not assume database rollback is safe; prefer forward-fix for destructive migrations.
- Verify data integrity and event replay implications after rollback.
- Maintain backup restore procedures and test them periodically.

## 6. Release checklist

- [ ] PRs reviewed and merged; CI green.
- [ ] Release notes and artifact versions recorded.
- [ ] Configuration and secrets verified.
- [ ] Migrations reviewed and backup completed.
- [ ] Health/readiness and smoke tests pass.
- [ ] Dashboards and alerts active.
- [ ] Rollback/forward-fix owner and procedure known.
- [ ] Product owner approves release.
