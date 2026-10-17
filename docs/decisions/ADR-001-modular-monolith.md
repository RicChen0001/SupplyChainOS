# ADR-001 — Modular monolith, not microservices

- Status: Accepted (Phase 1)
- Context: Single-team portfolio project; the hardest correctness problem is a
  single-transaction inventory reservation across recommendation → approval →
  ship → receive.
- Decision: One Spring Boot deployable, packages per module
  (`controller/service/repository/entity`), one PostgreSQL database.
- Consequences: + one transaction boundary for transfers; + one deployable to
  operate; − modules must respect package boundaries (no cross-module entity
  access except via services) to stay extractable later.
- Revisit when: an independent scaling / deploy / team-ownership need appears
  for a specific module. Splitting would start with the import pipeline (the
  most separable workload).
