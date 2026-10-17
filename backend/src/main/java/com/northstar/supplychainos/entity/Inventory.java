package com.northstar.supplychainos.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Inventory is the source of truth for stock. The frontend never computes it.
 * available = quantityOnHand - reservedQuantity (enforced by DB check constraints).
 */
@Entity
@Table(name = "inventory",
    uniqueConstraints = @UniqueConstraint(name = "uq_inventory_warehouse_product",
        columnNames = {"warehouse_id", "product_id"}))
public class Inventory {
  @Id @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private Warehouse warehouse;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "quantity_on_hand", nullable = false)
  private int quantityOnHand;

  @Column(name = "reserved_quantity", nullable = false)
  private int reservedQuantity;

  @Column(name = "reorder_threshold", nullable = false)
  private int reorderThreshold;

  @Version
  private long version; // optimistic locking for concurrent updates (Phase 5 also adds SELECT FOR UPDATE)

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();

  protected Inventory() {}
  public Inventory(Warehouse warehouse, Product product, int quantityOnHand, int reservedQuantity, int reorderThreshold) {
    this.warehouse = warehouse; this.product = product;
    this.quantityOnHand = quantityOnHand; this.reservedQuantity = reservedQuantity;
    this.reorderThreshold = reorderThreshold;
  }
  public UUID getId() { return id; }
  public Warehouse getWarehouse() { return warehouse; }
  public Product getProduct() { return product; }
  public int getQuantityOnHand() { return quantityOnHand; }
  public int getReservedQuantity() { return reservedQuantity; }
  /** Derived, never stored: available = on_hand - reserved. */
  @Transient
  public int getAvailableQuantity() { return quantityOnHand - reservedQuantity; }
  public int getReorderThreshold() { return reorderThreshold; }
  public long getVersion() { return version; }
  public Instant getUpdatedAt() { return updatedAt; }
}
