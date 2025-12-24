package com.fleet.auth_service.application.useCase.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fleet.auth_service.domain.model.RefreshToken;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import com.fleet.auth_service.infra.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class LogoutUseCase {
  private static final Logger log = LoggerFactory.getLogger(LogoutUseCase.class);
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenJwtService tokenJwtService;
  private final RedisService redisService;

  @Autowired
  public LogoutUseCase(RefreshTokenRepository refreshTokenRepository, TokenJwtService tokenJwtService, RedisService redisService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.tokenJwtService = tokenJwtService;
    this.redisService = redisService;
  }

  @Transactional
  public void execute(String refreshToken, String bearerToken) {
    try {
      if (bearerToken != null && bearerToken.startsWith("Bearer ")){

          String accessToken = bearerToken.substring(7);
          DecodedJWT decodedAccess = tokenJwtService.validateAndDecode(accessToken);
          Duration durationAccessToken = getAccessTokenRemainTime(decodedAccess);

          if(durationAccessToken.getSeconds() > 0) {
            redisService.addBlacklistJti(UUID.fromString(decodedAccess.getSubject()), UUID.fromString(decodedAccess.getId()), durationAccessToken);
          }

      }
    } catch (Exception e) {
      log.info("Tried to make logout with expired or invalid token");
    }

    String tokenHash = TokenJwtService.hashToken(refreshToken);

    Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByTokenHash(tokenHash);
    if (tokenOptional.isPresent()) {
      RefreshToken tokenEntity = tokenOptional.get();

      redisService.deleteSession(tokenEntity.getUser().getId(), tokenEntity.getSessionId());

      tokenEntity.setRevoked(true);
      refreshTokenRepository.save(tokenEntity);
    }
  }

  public Duration getAccessTokenRemainTime(DecodedJWT decodedAccess) {
    Instant expiresAt = decodedAccess.getExpiresAtAsInstant();
    Instant now = Instant.now();
    return Duration.between(now, expiresAt);
  }
}
