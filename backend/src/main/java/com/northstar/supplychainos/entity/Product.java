package com.northstar.supplychainos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
  @Id @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String sku;

  @Column(nullable = false, length = 300)
  private String name;

  @Column(nullable = false, length = 100)
  private String category;

  @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal unitPrice;

  @Column(name = "lead_time_days", nullable = false)
  private int leadTimeDays;

  @Column(name = "safety_stock", nullable = false)
  private int safetyStock;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Product() {}
  public Product(String sku, String name, String category, BigDecimal unitPrice, int leadTimeDays, int safetyStock) {
    this.sku = sku; this.name = name; this.category = category;
    this.unitPrice = unitPrice; this.leadTimeDays = leadTimeDays; this.safetyStock = safetyStock;
  }
  public UUID getId() { return id; }
  public String getSku() { return sku; }
  public String getName() { return name; }
  public String getCategory() { return category; }
  public BigDecimal getUnitPrice() { return unitPrice; }
  public int getLeadTimeDays() { return leadTimeDays; }
  public int getSafetyStock() { return safetyStock; }
  public boolean isActive() { return active; }
  public Instant getCreatedAt() { return createdAt; }
}
