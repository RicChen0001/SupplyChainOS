package com.northstar.supplychainos.dto;

import java.util.UUID;

public record UserDto(UUID id, String email, String fullName, String role) {}
