package com.northstar.supplychainos.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(UUID id, String sku, String name, String category,
                         BigDecimal unitPrice, int leadTimeDays, int safetyStock, boolean active) {}
