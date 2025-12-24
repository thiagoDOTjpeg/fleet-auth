package com.fleet.auth_service.domain.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "refresh_tokens")
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "token_hash", nullable = false, unique = true)
  private String tokenHash;

  @Column(name = "parent_id")
  private UUID parentId;

  @Column(name = "session_id")
  private UUID sessionId;

  @Column(nullable = false)
  private boolean used;

  @Column(nullable = false)
  private boolean revoked;

  @Version
  private Long version;

  @Column(name =  "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public RefreshToken() {}

  public RefreshToken(User user, String tokenHash, UUID parentId, UUID sessionId, Instant expiryDate) {
    this.user = user;
    this.tokenHash = tokenHash;
    this.parentId = parentId;
    this.sessionId = sessionId;
    this.expiresAt = expiryDate;
    this.used = false;
    this.revoked = false;
    this.createdAt = Instant.now();
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public void setSessionId(UUID sessionId) {
    this.sessionId = sessionId;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public void setTokenHash(String tokenHash) {
    this.tokenHash = tokenHash;
  }

  public UUID getParentId() {
    return parentId;
  }

  public void setParentId(UUID parentId) {
    this.parentId = parentId;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

  public boolean isRevoked() {
    return revoked;
  }

  public void setRevoked(boolean revoked) {
    this.revoked = revoked;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant expiryDate) {
    this.expiresAt = expiryDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
