package com.fleet.auth_service;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractIntegrationTest {
  @SuppressWarnings("resource")
  static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:14-3.3").asCompatibleSubstituteFor("postgres")
  ).withInitScript("init-schema.sql");

  static {
    postgres.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    registry.add("spring.flyway.enabled", () -> "true");
  }
}
