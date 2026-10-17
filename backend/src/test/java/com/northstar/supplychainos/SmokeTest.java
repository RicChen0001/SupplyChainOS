package com.northstar.supplychainos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Phase 1 smoke test: app boots, health endpoint works, unauthenticated
 * access to protected APIs is rejected (security is actually enforced).
 * Uses H2 (test profile) — NOT a substitute for the Testcontainers/Postgres
 * integration tests that land in later phases.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SmokeTest {

  @Autowired MockMvc mockMvc;

  @Test
  void healthIsPublic() throws Exception {
    mockMvc.perform(get("/api/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.status").value("UP"));
  }

  @Test
  void protectedApiRequiresAuth() throws Exception {
    mockMvc.perform(get("/api/warehouses"))
        .andExpect(status().isUnauthorized());
  }
}
