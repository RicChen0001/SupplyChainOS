package com.northstar.supplychainos.service;

import com.northstar.supplychainos.dto.PageResponse;
import com.northstar.supplychainos.dto.ProductDto;
import com.northstar.supplychainos.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {
  private final ProductRepository products;
  public ProductService(ProductRepository products) { this.products = products; }

  public PageResponse<ProductDto> list(int page, int size) {
    var p = products.findAllByActiveTrue(PageRequest.of(page, Math.min(size, 100)));
    var items = p.stream()
        .map(x -> new ProductDto(x.getId(), x.getSku(), x.getName(), x.getCategory(),
            x.getUnitPrice(), x.getLeadTimeDays(), x.getSafetyStock(), x.isActive()))
        .toList();
    return new PageResponse<>(items, p.getTotalElements(), page, size, p.getTotalPages());
  }
}
