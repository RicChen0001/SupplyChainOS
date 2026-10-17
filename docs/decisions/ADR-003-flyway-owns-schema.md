# ADR-003 — Flyway owns the schema; Hibernate validates only

- Status: Accepted (Phase 1)
- Context: Schema changes must be reviewable, repeatable, and reversible-ish.
- Decision: `spring.jpa.hibernate.ddl-auto: validate`; every schema change is a
  versioned `V__` SQL migration. Tests use H2 `create-drop` only.
- Consequences: + schema is explicit in git; + prod and dev run the same DDL;
  − developers must write migrations (intentional).
