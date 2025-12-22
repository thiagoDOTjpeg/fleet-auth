package com.fleet.auth_service.application.useCase.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.domain.model.RefreshToken;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.domain.model.UserSession;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import com.fleet.auth_service.infra.repository.RefreshTokenRepository;
import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenUseCase {
  private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenJwtService tokenJwtService;
  private final RedisService redisService;
  private final UserMapper userMapper;

  @Autowired
  public RefreshTokenUseCase(TokenJwtService tokenJwtService, RedisService redisService, UserMapper userMapper, RefreshTokenRepository refreshTokenRepository) {
    this.tokenJwtService = tokenJwtService;
    this.redisService = redisService;
    this.userMapper = userMapper;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Transactional
  public TokenResponse execute(String rawRefreshToken, String ipAddress, String userAgent) {

    try {
      tokenJwtService.validateAndDecode(rawRefreshToken);
    } catch (Exception e) {
      throw new UnauthorizedException("Invalid refresh token");
    }

    String tokenHash = TokenJwtService.hashToken(rawRefreshToken);

    RefreshToken currentToken = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));


    if(currentToken.isRevoked()) {
      refreshTokenRepository.revokeAllActiveByUser(currentToken.getUser().getId());
      redisService.deleteAllSession(currentToken.getUser().getId());

      log.warn("Refresh token re-use try");
      throw new UnauthorizedException("Invalid refresh token");
    }

    if(currentToken.getExpiresAt().isBefore(Instant.now())) {
      throw new UnauthorizedException("Invalid refresh token");
    }

    currentToken.setRevoked(true);
    try {
      refreshTokenRepository.save(currentToken);
    } catch (OptimisticLockingFailureException e) {
      log.info("Race condition detected for token refresh. Skipping revocation logic.");
      throw new UnauthorizedException("Refresh token processed by another thread");
    }

    User user = currentToken.getUser();

    String newAccessToken = tokenJwtService.generateAccessToken(user);
    String newRefreshToken = tokenJwtService.generateRefreshToken(user);
    String newRefreshTokenHash = TokenJwtService.hashToken(newRefreshToken);

    UserSession session = new UserSession(
            UUID.randomUUID(),
            ipAddress,
            userAgent,
            Instant.now(),
            user.getId(),
            newRefreshTokenHash
    );
    redisService.saveSession(user.getId(), session, Duration.ofDays(7));
    redisService.deleteSession(user.getId(), currentToken.getSessionId());

    RefreshToken newTokenEntity = new RefreshToken(
            user,
            newRefreshTokenHash,
            currentToken.getId(),
            session.getSessionId(),
            Instant.now().plus(7, ChronoUnit.DAYS)
    );
    refreshTokenRepository.save(newTokenEntity);

    return new TokenResponse(newAccessToken, newRefreshToken, userMapper.toUserSummary(user));

  }
}
