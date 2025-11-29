package com.fleet.auth_service.config;

import com.fleet.auth_service.config.properties.FlywayProperties;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

  private final FlywayProperties flywayProperties;

  public FlywayConfig(FlywayProperties flywayProperties) {
    this.flywayProperties = flywayProperties;
  }

  @Bean
  public Flyway flyway(DataSource dataSource) {
    Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations(flywayProperties.getLocations())
            .baselineOnMigrate(flywayProperties.getBaselineOnMigrate())
            .baselineVersion(flywayProperties.getBaselineVersion())
            .validateOnMigrate(flywayProperties.getValidateOnMigrate())

            .schemas(flywayProperties.getSchemas())
            .defaultSchema(flywayProperties.getDefaultSchema())
            .createSchemas(true)

            .load();

    if (flywayProperties.getEnabled()) {
      flyway.migrate();
    }

    return flyway;
  }
}