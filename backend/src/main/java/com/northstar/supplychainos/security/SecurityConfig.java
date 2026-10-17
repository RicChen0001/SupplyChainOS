package com.northstar.supplychainos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.northstar.supplychainos.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * Session-based auth: HttpOnly session cookie + CSRF token cookie.
 * Same-origin deployment (frontend served behind the same origin / Vite proxy in dev)
 * avoids cross-origin auth complexity. No secrets in frontend code.
 * Unauthenticated / forbidden API calls return JSON (401 / 403), never a login redirect.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, ObjectMapper mapper) throws Exception {
    var csrfHandler = new CsrfTokenRequestAttributeHandler();
    csrfHandler.setCsrfRequestAttributeName(null);
    http
      .csrf(csrf -> csrf
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
          .csrfTokenRequestHandler(csrfHandler)
          .ignoringRequestMatchers("/api/auth/login", "/api/auth/logout", "/actuator/health"))
      .exceptionHandling(ex -> ex
          .authenticationEntryPoint((req, res, e) -> writeError(res, mapper, 401, "UNAUTHORIZED",
              "Authentication required or invalid credentials."))
          .accessDeniedHandler((req, res, e) -> writeError(res, mapper, 403, "FORBIDDEN",
              "You do not have permission to perform this action.")))
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/actuator/health", "/actuator/info", "/api/health").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
          .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
          .requestMatchers("/api/**").authenticated()
          .anyRequest().permitAll())
      .formLogin(form -> form.disable())
      .httpBasic(basic -> basic.disable())
      .logout(logout -> logout
          .logoutUrl("/api/auth/logout")
          .deleteCookies("JSESSIONID", "XSRF-TOKEN")
          .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)));
    return http.build();
  }

  private static void writeError(HttpServletResponse res, ObjectMapper mapper,
                                 int status, String code, String message) throws java.io.IOException {
    res.setStatus(status);
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    mapper.writeValue(res.getWriter(), ApiResponse.error(code, message));
  }
}
