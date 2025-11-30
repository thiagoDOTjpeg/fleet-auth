package com.fleet.auth_service.infra.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.flyway")
public class FlywayProperties {

  private Boolean enabled = true;
  private Boolean baselineOnMigrate = true;
  private String[] locations = {"classpath:db/migration"};
  private String baselineVersion = "0";
  private Boolean validateOnMigrate = true;
  private String[] schemas = {"auth"};
  private String defaultSchema = "auth";
  private Boolean createSchemas = false;

  public Boolean getEnabled() { return enabled; }
  public void setEnabled(Boolean enabled) { this.enabled = enabled; }
  public Boolean getBaselineOnMigrate() { return baselineOnMigrate; }
  public void setBaselineOnMigrate(Boolean baselineOnMigrate) { this.baselineOnMigrate = baselineOnMigrate; }
  public String[] getLocations() { return locations; }
  public void setLocations(String[] locations) { this.locations = locations; }
  public String getBaselineVersion() { return baselineVersion; }
  public void setBaselineVersion(String baselineVersion) { this.baselineVersion = baselineVersion; }
  public Boolean getValidateOnMigrate() { return validateOnMigrate; }
  public void setValidateOnMigrate(Boolean validateOnMigrate) { this.validateOnMigrate = validateOnMigrate; }

  public String[] getSchemas() {
    return schemas;
  }

  public void setSchemas(String[] schemas) {
    this.schemas = schemas;
  }

  public String getDefaultSchema() {
    return defaultSchema;
  }

  public void setDefaultSchema(String defaultSchema) {
    this.defaultSchema = defaultSchema;
  }

  public Boolean getCreateSchemas() {
    return createSchemas;
  }

  public void setCreateSchemas(Boolean createSchemas) {
    this.createSchemas = createSchemas;
  }
}