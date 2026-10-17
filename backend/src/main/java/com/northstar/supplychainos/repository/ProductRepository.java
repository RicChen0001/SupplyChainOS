package com.northstar.supplychainos.repository;

import com.northstar.supplychainos.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  Optional<Product> findBySku(String sku);
  Page<Product> findAllByActiveTrue(Pageable pageable);
}
