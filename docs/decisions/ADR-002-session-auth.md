# ADR-002 — Session auth with HttpOnly cookies + CSRF (not JWT)

- Status: Accepted (Phase 1)
- Context: Same-origin web app; no third-party API consumers in the MVP.
- Decision: Server-side sessions, HttpOnly `SameSite=Lax` session cookie
  (`Secure` in production), `XSRF-TOKEN` cookie + header for CSRF.
- Consequences: + logout is server-side revocation (no token-revocation problem);
  + no tokens in localStorage / JS; − sessions are stateful (fine for one instance;
  sticky sessions / shared store if ever scaled horizontally).
