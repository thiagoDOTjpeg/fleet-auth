package com.fleet.auth_service.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public class UserSession {
  @JsonProperty("s_id")
  private UUID sessionId;
  @JsonProperty("u_ip")
  private String ipAddress;
  @JsonProperty("u_dev")
  private String deviceInfo;
  @JsonProperty("u_ait")
  private Instant issuedAt;
  @JsonProperty("u_id")
  private UUID userId;
  @JsonProperty("rt_hash")
  private String refreshTokenHashed;

  public UserSession() {
  }

  public UserSession(UUID sessionId, String ipAddress, String deviceInfo, Instant issuedAt, UUID userId, String refreshTokenHashed) {
    this.sessionId = sessionId;
    this.ipAddress = ipAddress;
    this.deviceInfo = deviceInfo;
    this.issuedAt = issuedAt;
    this.userId = userId;
    this.refreshTokenHashed = refreshTokenHashed;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public void setSessionId(UUID sessionId) {
    this.sessionId = sessionId;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getDeviceInfo() {
    return deviceInfo;
  }

  public void setDeviceInfo(String deviceInfo) {
    this.deviceInfo = deviceInfo;
  }

  public Instant getIssuedAt() {
    return issuedAt;
  }

  public void setIssuedAt(Instant issuedAt) {
    this.issuedAt = issuedAt;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getRefreshTokenHashed() {
    return refreshTokenHashed;
  }

  public void setRefreshTokenHashed(String refreshTokenHashed) {
    this.refreshTokenHashed = refreshTokenHashed;
  }
}
