package com.northstar.supplychainos.dto;

public record DashboardOverviewDto(
    long totalWarehouses,
    long totalProducts,
    long totalInventoryUnits,
    long totalInventoryRecords,
    String dataSource) {}
