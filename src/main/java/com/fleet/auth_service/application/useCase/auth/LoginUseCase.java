package com.fleet.auth_service.application.useCase.auth;

import com.fleet.auth_service.application.dto.request.LoginRequest;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.domain.model.RefreshToken;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.domain.model.UserSession;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import com.fleet.auth_service.infra.repository.RefreshTokenRepository;
import com.fleet.auth_service.shared.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class LoginUseCase {
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenJwtService tokenJwtService;
  private final RedisService redisService;
  private final UserMapper userMapper;

  @Autowired
  public LoginUseCase(AuthenticationManager authenticationManager, TokenJwtService tokenJwtService, RedisService redisService,  UserMapper userMapper, RefreshTokenRepository refreshTokenRepository) {
    this.authenticationManager = authenticationManager;
    this.refreshTokenRepository = refreshTokenRepository;
    this.tokenJwtService = tokenJwtService;
    this.redisService = redisService;
    this.userMapper = userMapper;
  }

  @Transactional
  public TokenResponse execute(LoginRequest request, String ipAddress, String userAgent) {
    Authentication auth;
    String usernameComposite = request.email() + ":" + request.userType();
    try {
      auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameComposite, request.password()));

    } catch (AuthenticationException e) {
      throw new UnauthorizedException("Email or password is invalid/wrong");
    }

    User user = (User) auth.getPrincipal();

    if(user == null) throw new UnauthorizedException("Invalid email or password");

    if (!user.getUserType().equals(request.userType())) {
      throw new UnauthorizedException("User type is invalid for this request");
    }

    String accessToken = tokenJwtService.generateAccessToken(user);
    String refreshToken = tokenJwtService.generateRefreshToken(user);
    String refreshTokenHashed = TokenJwtService.hashToken(refreshToken);

    UserSession session = new UserSession(UUID.randomUUID(), ipAddress, userAgent, Instant.now().plus(7, ChronoUnit.DAYS), user.getId(), refreshTokenHashed);
    refreshTokenRepository.save(new RefreshToken(user, refreshTokenHashed, null, session.getSessionId(),Instant.now().plus(7, ChronoUnit.DAYS)));

    redisService.saveSession(user.getId(), session, Duration.ofDays(7));

    return new TokenResponse(accessToken, refreshToken, userMapper.toUserSummary(user));

  }
}
