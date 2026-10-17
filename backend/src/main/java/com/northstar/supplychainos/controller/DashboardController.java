package com.northstar.supplychainos.controller;

import com.northstar.supplychainos.dto.ApiResponse;
import com.northstar.supplychainos.dto.DashboardOverviewDto;
import com.northstar.supplychainos.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
  private final DashboardService service;
  public DashboardController(DashboardService service) { this.service = service; }

  @GetMapping("/overview")
  public ApiResponse<DashboardOverviewDto> overview() {
    return ApiResponse.ok(service.overview());
  }
}
