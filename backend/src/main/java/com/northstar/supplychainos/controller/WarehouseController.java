package com.northstar.supplychainos.controller;

import com.northstar.supplychainos.dto.ApiResponse;
import com.northstar.supplychainos.dto.WarehouseDto;
import com.northstar.supplychainos.service.WarehouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
  private final WarehouseService service;
  public WarehouseController(WarehouseService service) { this.service = service; }

  @GetMapping
  public ApiResponse<List<WarehouseDto>> list() {
    return ApiResponse.ok(service.listActive());
  }
}
