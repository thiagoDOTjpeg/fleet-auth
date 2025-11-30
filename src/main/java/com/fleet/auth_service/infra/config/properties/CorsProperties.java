package com.fleet.auth_service.infra.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

  private List<String> allowedOrigins = new ArrayList<>(List.of(
          "http://localhost:4200",
          "http://localhost:3000"
  ));

  private List<String> allowedMethods = new ArrayList<>(List.of(
          "GET", "POST", "PUT", "DELETE", "OPTIONS"
  ));

  private List<String> allowedHeaders = new ArrayList<>(List.of("*"));
  private Boolean allowCredentials = true;
  private Long maxAge = 3600L;

  // Getters and Setters
  public List<String> getAllowedOrigins() {
    return allowedOrigins;
  }

  public void setAllowedOrigins(List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  public List<String> getAllowedMethods() {
    return allowedMethods;
  }

  public void setAllowedMethods(List<String> allowedMethods) {
    this.allowedMethods = allowedMethods;
  }

  public List<String> getAllowedHeaders() {
    return allowedHeaders;
  }

  public void setAllowedHeaders(List<String> allowedHeaders) {
    this.allowedHeaders = allowedHeaders;
  }

  public Boolean getAllowCredentials() {
    return allowCredentials;
  }

  public void setAllowCredentials(Boolean allowCredentials) {
    this.allowCredentials = allowCredentials;
  }

  public Long getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(Long maxAge) {
    this.maxAge = maxAge;
  }
}