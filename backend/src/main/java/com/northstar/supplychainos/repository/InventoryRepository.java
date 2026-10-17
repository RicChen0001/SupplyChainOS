package com.northstar.supplychainos.repository;

import com.northstar.supplychainos.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
  /** Total units on hand across all warehouses — dashboard metric from a real query. */
  @Query("SELECT COALESCE(SUM(i.quantityOnHand), 0) FROM Inventory i")
  long sumQuantityOnHand();
}
