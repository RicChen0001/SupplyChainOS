package com.northstar.supplychainos.repository;

import com.northstar.supplychainos.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
  Optional<Warehouse> findByCode(String code);
  List<Warehouse> findAllByActiveTrueOrderByCodeAsc();
}
