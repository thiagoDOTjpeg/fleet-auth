package com.fleet.auth_service.application.useCase.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.domain.model.UserSession;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenUseCase {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenJwtService tokenJwtService;
  private final RedisService redisService;
  private final UserMapper userMapper;

  @Autowired
  public RefreshTokenUseCase(PasswordEncoder passwordEncoder, TokenJwtService tokenJwtService, RedisService redisService, UserMapper userMapper, UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.tokenJwtService = tokenJwtService;
    this.redisService = redisService;
    this.userMapper = userMapper;
    this.userRepository = userRepository;
  }

  public TokenResponse execute(String refreshToken, String accessToken, String ipAddress, String userAgent) {
    DecodedJWT decodedRefresh = tokenJwtService.validateAndDecode(refreshToken);
    DecodedJWT decodedAccess = tokenJwtService.decodeToken(accessToken);

    if(!decodedRefresh.getSubject().equals(decodedAccess.getSubject())) throw new UnauthorizedException("Acesso Negado");

    User user = userRepository.findById(UUID.fromString(decodedRefresh.getSubject())).orElseThrow(() -> new UnauthorizedException("Acesso negado"));
    Optional<UserSession> isBlacklisted = redisService.get("session:blacklist:"+user.getId(), UserSession.class);
    if(isBlacklisted.isPresent()) throw new UnauthorizedException("Acesso negado");

    UserSession userSession = redisService.get("session:"+user.getId(), UserSession.class).orElseThrow(() -> new UnauthorizedException("Acesso negado"));
    if(!passwordEncoder.matches(refreshToken, userSession.getRefreshTokenHashed())) throw new UnauthorizedException("Acesso negado");

    String newAccessToken = tokenJwtService.generateAccessToken(user);
    String newRefreshToken = tokenJwtService.generateRefreshToken(user);

    String hashedRefreshToken = passwordEncoder.encode(newRefreshToken);

    UserSession newSession = new UserSession(UUID.randomUUID(), ipAddress, userAgent, Instant.now(), user.getId(), hashedRefreshToken);

    redisService.set("session:blacklist:"+user.getId(), userSession, Duration.ofDays(7));
    redisService.set("session:"+user.getId(), newSession, Duration.ofDays(7));

    return new TokenResponse(newAccessToken, newRefreshToken, userMapper.toUserSummary(user));
  }
}
