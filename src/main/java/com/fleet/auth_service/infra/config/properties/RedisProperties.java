package com.fleet.auth_service.infra.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.redis")
public class RedisProperties {

  private Long sessionTtl = 86400000L; // 24 horas em ms
  private Long tokenBlacklistTtl = 604800000L; // 7 dias em ms

  public Long getSessionTtl() {
    return sessionTtl;
  }

  public void setSessionTtl(Long sessionTtl) {
    this.sessionTtl = sessionTtl;
  }

  public Long getTokenBlacklistTtl() {
    return tokenBlacklistTtl;
  }

  public void setTokenBlacklistTtl(Long tokenBlacklistTtl) {
    this.tokenBlacklistTtl = tokenBlacklistTtl;
  }
}