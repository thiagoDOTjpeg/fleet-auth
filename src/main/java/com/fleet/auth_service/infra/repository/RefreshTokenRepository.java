package com.fleet.auth_service.infra.repository;

import com.fleet.auth_service.domain.model.RefreshToken;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  @Modifying
  @Query("UPDATE refresh_tokens r SET r.revoked = true WHERE r.user.id = :userId")
  void revokeAllByUser(UUID userId);

  @Modifying
  @Query("UPDATE refresh_tokens r SET r.revoked = true WHERE r.user.id = :userId AND r.revoked = false")
  void revokeAllActiveByUser(UUID userId);
}
