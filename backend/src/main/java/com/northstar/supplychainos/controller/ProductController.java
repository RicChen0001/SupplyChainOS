package com.northstar.supplychainos.controller;

import com.northstar.supplychainos.dto.ApiResponse;
import com.northstar.supplychainos.dto.PageResponse;
import com.northstar.supplychainos.dto.ProductDto;
import com.northstar.supplychainos.service.ProductService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService service;
  public ProductController(ProductService service) { this.service = service; }

  @GetMapping
  public ApiResponse<PageResponse<ProductDto>> list(
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return ApiResponse.ok(service.list(page, size));
  }
}
