# API

Base: same-origin `/api`. Envelope for every response:

```json
{ "success": true, "data": { "...": "..." }, "error": null, "timestamp": "2026-10-17T08:00:00Z" }
```

Errors: `{ "success": false, "data": null, "error": { "code": "NOT_FOUND", "message": "..." } }`.
No stack traces, no DB internals. Pagination: `?page=0&size=20` → `{ items, total, page, size, totalPages }`.

## Implemented in Phase 1

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | /api/health | Public | `{"status":"UP"}` |
| POST | /api/auth/login | Public | `{email, password}` → sets HttpOnly session cookie |
| POST | /api/auth/logout | Auth | 204 |
| GET | /api/auth/me | Auth | Current user |
| GET | /api/warehouses | Auth | 3 seeded warehouses |
| GET | /api/products?page&size | Auth | Paged, active products (empty until Phase 2 seeds data) |
| GET | /api/dashboard/overview | Auth | Real counts from Postgres |

## Planned (Phases 2–5, per spec §7)

Imports, risks, recommendations (+approve/reject), transfers (+ship/receive/cancel),
shipments, orders, inventory (+low-stock, movements), alerts, audit-logs.
Any deviation from the spec’s suggested contracts will be documented here when
the endpoint ships — no endpoint is claimed before it exists.
