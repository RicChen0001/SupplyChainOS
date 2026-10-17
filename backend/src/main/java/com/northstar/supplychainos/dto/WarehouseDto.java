package com.northstar.supplychainos.dto;

import java.util.UUID;

public record WarehouseDto(UUID id, String code, String name, String city, String state,
                           String country, String timezone, boolean active) {}
