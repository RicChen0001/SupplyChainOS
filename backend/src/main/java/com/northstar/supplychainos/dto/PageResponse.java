package com.northstar.supplychainos.dto;

import java.util.List;

public record PageResponse<T>(List<T> items, long total, int page, int size, int totalPages) {}
