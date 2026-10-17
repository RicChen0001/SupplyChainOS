package com.northstar.supplychainos.controller;

import com.northstar.supplychainos.dto.ApiResponse;
import com.northstar.supplychainos.dto.LoginRequest;
import com.northstar.supplychainos.dto.UserDto;
import com.northstar.supplychainos.entity.User;
import com.northstar.supplychainos.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthenticationManager authManager;
  private final UserRepository users;

  public AuthController(AuthenticationManager authManager, UserRepository users) {
    this.authManager = authManager; this.users = users;
  }

  @PostMapping("/login")
  public ApiResponse<UserDto> login(@Valid @RequestBody LoginRequest req,
                                    HttpServletRequest httpReq, HttpServletResponse httpRes) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    var context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);
    httpReq.getSession(true).setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    User user = users.findByEmail(req.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    return ApiResponse.ok(new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRole()));
  }

  @GetMapping("/me")
  public ApiResponse<UserDto> me(Authentication auth) {
    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    User user = users.findByEmail(auth.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    return ApiResponse.ok(new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRole()));
  }
}
