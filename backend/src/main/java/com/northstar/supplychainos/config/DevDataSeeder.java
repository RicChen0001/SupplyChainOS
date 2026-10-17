package com.northstar.supplychainos.config;

import com.northstar.supplychainos.entity.User;
import com.northstar.supplychainos.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Dev-only seed: creates ONE admin user if the users table is empty.
 * Active only on `dev` / `docker` profiles. Never runs in production.
 * Password comes from SEED_ADMIN_PASSWORD env only; there is no committed default.
 */
@Component
@Profile({"dev", "docker"})
public class DevDataSeeder implements ApplicationRunner {
  private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final String adminEmail;
  private final String adminPassword;
  private final String adminName;

  public DevDataSeeder(UserRepository users, PasswordEncoder encoder,
                       @Value("${app.seed.admin-email}") String adminEmail,
                       @Value("${app.seed.admin-password}") String adminPassword,
                       @Value("${app.seed.admin-name}") String adminName) {
    this.users = users; this.encoder = encoder;
    this.adminEmail = adminEmail; this.adminPassword = adminPassword; this.adminName = adminName;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (adminPassword == null || adminPassword.isBlank()) {
      log.warn("DEV SEED skipped: SEED_ADMIN_PASSWORD is not set. No demo admin user was created.");
      return;
    }
    if (users.count() == 0) {
      users.save(new User(adminEmail, encoder.encode(adminPassword), adminName, "ADMIN"));
      log.warn("DEV SEED: created demo admin {} — this is a local-demo account only.", adminEmail);
    }
  }
}
