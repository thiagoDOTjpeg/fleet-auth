package com.fleet.auth_service.infra.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String secret = "changeme-this-is-a-very-secret-key-for-development-only";
  private String issuer = "fleet-auth-service";
  private String audience = "fleet-platform";
  private String header = "Authorization";
  private String prefix = "Bearer ";
  private Expiration expiration = new Expiration();

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public Expiration getExpiration() {
    return expiration;
  }

  public void setExpiration(Expiration expiration) {
    this.expiration = expiration;
  }

  public static class Expiration {
    private Long accessToken = 900000L;
    private Long refreshToken = 604800000L;

    public Long getAccessToken() {
      return accessToken;
    }

    public void setAccessToken(Long accessToken) {
      this.accessToken = accessToken;
    }

    public Long getRefreshToken() {
      return refreshToken;
    }

    public void setRefreshToken(Long refreshToken) {
      this.refreshToken = refreshToken;
    }
  }
}