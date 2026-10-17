package com.northstar.supplychainos.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
  public ResourceNotFoundException(String resource, String id) {
    super(HttpStatus.NOT_FOUND, "NOT_FOUND", resource + " not found: " + id);
  }
}
