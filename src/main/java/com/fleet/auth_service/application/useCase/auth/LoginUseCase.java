package com.fleet.auth_service.application.useCase.auth;

import com.fleet.auth_service.application.dto.request.LoginRequest;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.domain.model.UserSession;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class LoginUseCase {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final TokenJwtService tokenJwtService;
  private final RedisService redisService;
  private final UserMapper userMapper;

  @Autowired
  public LoginUseCase(AuthenticationManager authenticationManager, TokenJwtService tokenJwtService, RedisService redisService,  PasswordEncoder passwordEncoder,  UserMapper userMapper) {
    this.authenticationManager = authenticationManager;
    this.tokenJwtService = tokenJwtService;
    this.redisService = redisService;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
  }

  public TokenResponse execute(LoginRequest request, String ipAddress, String userAgent) {
    Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    User user = (User) auth.getPrincipal();

    String accessToken = tokenJwtService.generateAccessToken(user);
    String refreshToken = tokenJwtService.generateRefreshToken(user);

    String refreshTokenHashed = passwordEncoder.encode(refreshToken);

    UserSession userSession = new UserSession(UUID.randomUUID(), ipAddress, userAgent,Instant.now(), Objects.requireNonNull(user).getId(), refreshTokenHashed);
    redisService.saveSession(user.getId(), userSession, Duration.ofDays(7));
    return new TokenResponse(accessToken, refreshToken, userMapper.toUserSummary(user));
  }
}
