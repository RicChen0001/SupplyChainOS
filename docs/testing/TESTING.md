# Testing strategy

## Phase 1 (implemented)

- `SmokeTest` (JUnit 5 + MockMvc, H2): app boots, `/api/health` is public,
  `/api/warehouses` rejects unauthenticated requests. Proves security is
  enforced, not just configured.
- Frontend `format.test.ts` (Vitest): number formatting helper.

## Planned (with the modules they cover)

| Category | Tool | When |
|---|---|---|
| Inventory rules, risk calc, recommendation calc | JUnit 5 + Mockito (pure unit) | Phases 3–4 |
| Transfer state transitions, transactional updates | Spring Boot Test | Phase 5 |
| Concurrent transfer approval (Scenario D) | Spring Boot Test + threads | Phase 5 |
| CSV validation, idempotent re-import | Spring Boot Test + Python tests | Phase 2 |
| Auth / authorization (role matrix) | Spring Boot Test + MockMvc | Phase 5 |
| Postgres integration | Testcontainers | Phases 2–5 |
| Critical user workflows | Playwright | Phase 6 |
| Frontend flows | Vitest + Testing Library | Phases 3–6 |

Rule from the spec, enforced here: **a test is only claimed passing if it was
actually executed** (see Phase 1 report in the delivery message / git history).
