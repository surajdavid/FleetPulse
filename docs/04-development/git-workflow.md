# FleetPulse – Git Workflow

**Status:** Proposed workflow  
**Version:** 1.0

## 1. Branch model

- `main`: protected, releasable branch.
- Feature branches: `feature/<ticket>-<short-description>`.
- Bug fixes: `fix/<ticket>-<short-description>`.
- Urgent production fixes: `hotfix/<ticket>-<short-description>`.
- Documentation-only changes: `docs/<short-description>`.

Use the branch model already configured in the repository if it differs; update this document rather than maintaining two competing workflows.

## 2. Pull request lifecycle

1. Create a ticket with scope and acceptance criteria.
2. Branch from latest `main`.
3. Make a focused change; avoid unrelated formatting/refactoring.
4. Run backend tests/build, frontend lint/build and relevant integration checks.
5. Update API/event/database documentation where needed.
6. Push branch and open PR with purpose, implementation notes, test evidence, migration impact and screenshots for UI changes.
7. Obtain review and resolve comments.
8. Confirm required CI checks are green.
9. Merge using repository-approved strategy.
10. Delete merged branch and verify `main`.

## 3. Commit messages

Recommended format: `type(scope): imperative summary`

Examples:
- `feat(customer): add tenant-scoped customer creation`
- `test(shipment): cover invalid status transitions`
- `docs(architecture): document event envelope`
- `fix(inventory): prevent duplicate reservations`

Keep commits logically focused. Do not include secrets, generated binaries or unrelated files.

## 4. PR checklist

- [ ] Scope matches ticket and acceptance criteria.
- [ ] Tests added/updated and passing.
- [ ] Tenant isolation and authorization considered.
- [ ] API/event contracts and docs updated.
- [ ] Database migration is safe and reviewed.
- [ ] No secrets or sensitive data in code/logs.
- [ ] Formatting, lint and static checks pass.
- [ ] CI is green.
- [ ] Reviewer approval obtained.

## 5. Repository protection

Require PRs, successful CI and review before merging. Restrict force-push and deletion of `main`. Protect release tags. Use least-privilege repository tokens and rotate credentials if exposed.
