package com.northstar.supplychainos.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "warehouses")
public class Warehouse {
  @Id @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true, length = 20)
  private String code;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 100)
  private String city;

  @Column(nullable = false, length = 10)
  private String state;

  @Column(nullable = false, length = 100)
  private String country = "US";

  @Column(nullable = false, length = 50)
  private String timezone;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Warehouse() {}
  public Warehouse(String code, String name, String city, String state, String timezone) {
    this.code = code; this.name = name; this.city = city; this.state = state; this.timezone = timezone;
  }
  public UUID getId() { return id; }
  public String getCode() { return code; }
  public String getName() { return name; }
  public String getCity() { return city; }
  public String getState() { return state; }
  public String getCountry() { return country; }
  public String getTimezone() { return timezone; }
  public boolean isActive() { return active; }
  public Instant getCreatedAt() { return createdAt; }
}
