package com.northstar.supplychainos.dto;

import java.time.Instant;

/** Consistent envelope for all API responses. */
public record ApiResponse<T>(boolean success, T data, ApiError error, Instant timestamp) {
  public record ApiError(String code, String message) {}

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, data, null, Instant.now());
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    return new ApiResponse<>(false, null, new ApiError(code, message), Instant.now());
  }
}
