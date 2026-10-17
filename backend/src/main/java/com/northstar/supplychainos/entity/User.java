package com.northstar.supplychainos.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "full_name", nullable = false, length = 200)
  private String fullName;

  @Column(nullable = false, length = 30)
  private String role; // ADMIN | OPERATIONS_MANAGER | ANALYST

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();

  protected User() {}
  public User(String email, String passwordHash, String fullName, String role) {
    this.email = email; this.passwordHash = passwordHash; this.fullName = fullName; this.role = role;
  }
  public UUID getId() { return id; }
  public String getEmail() { return email; }
  public String getPasswordHash() { return passwordHash; }
  public String getFullName() { return fullName; }
  public String getRole() { return role; }
  public boolean isActive() { return active; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setActive(boolean active) { this.active = active; }
}
