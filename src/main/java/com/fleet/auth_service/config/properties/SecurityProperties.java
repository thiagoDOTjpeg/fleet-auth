package com.fleet.auth_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

  private RateLimit rateLimit = new RateLimit();

  public RateLimit getRateLimit() {
    return rateLimit;
  }

  public void setRateLimit(RateLimit rateLimit) {
    this.rateLimit = rateLimit;
  }

  public static class RateLimit {
    private LoginLimit login = new LoginLimit();
    private TokenRefreshLimit tokenRefresh = new TokenRefreshLimit();

    public LoginLimit getLogin() {
      return login;
    }

    public void setLogin(LoginLimit login) {
      this.login = login;
    }

    public TokenRefreshLimit getTokenRefresh() {
      return tokenRefresh;
    }

    public void setTokenRefresh(TokenRefreshLimit tokenRefresh) {
      this.tokenRefresh = tokenRefresh;
    }

    public static class LoginLimit {
      private Integer maxAttempts = 5;
      private Long blockDuration = 900000L; // 15 minutos

      public Integer getMaxAttempts() {
        return maxAttempts;
      }

      public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
      }

      public Long getBlockDuration() {
        return blockDuration;
      }

      public void setBlockDuration(Long blockDuration) {
        this.blockDuration = blockDuration;
      }
    }

    public static class TokenRefreshLimit {
      private Integer maxAttempts = 10;
      private Long blockDuration = 3600000L; // 1 hora

      public Integer getMaxAttempts() {
        return maxAttempts;
      }

      public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
      }

      public Long getBlockDuration() {
        return blockDuration;
      }

      public void setBlockDuration(Long blockDuration) {
        this.blockDuration = blockDuration;
      }
    }
  }
}