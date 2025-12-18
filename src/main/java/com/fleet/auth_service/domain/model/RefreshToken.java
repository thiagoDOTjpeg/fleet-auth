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

  @Column(nullable = false)
  private boolean used;

  @Column(nullable = false)
  private boolean revoked;

  @Column(name =  "expiry_date", nullable = false)
  private Instant expiryDate;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public RefreshToken() {}

  public RefreshToken(User user, String tokenHash, UUID parentId, Instant expiryDate) {
    this.user = user;
    this.tokenHash = tokenHash;
    this.parentId = parentId;
    this.expiryDate = expiryDate;
    this.used = false;
    this.revoked = false;
    this.createdAt = Instant.now();
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

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Instant expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
