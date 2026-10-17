package com.northstar.supplychainos.service;

import com.northstar.supplychainos.dto.DashboardOverviewDto;
import com.northstar.supplychainos.repository.InventoryRepository;
import com.northstar.supplychainos.repository.ProductRepository;
import com.northstar.supplychainos.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {
  private final WarehouseRepository warehouses;
  private final ProductRepository products;
  private final InventoryRepository inventory;

  public DashboardService(WarehouseRepository warehouses, ProductRepository products,
                          InventoryRepository inventory) {
    this.warehouses = warehouses; this.products = products; this.inventory = inventory;
  }

  /** All numbers come from real DB queries — no hardcoded dashboard metrics. */
  public DashboardOverviewDto overview() {
    return new DashboardOverviewDto(
        warehouses.count(),
        products.count(),
        inventory.sumQuantityOnHand(),
        inventory.count(),
        "postgresql");
  }
}
