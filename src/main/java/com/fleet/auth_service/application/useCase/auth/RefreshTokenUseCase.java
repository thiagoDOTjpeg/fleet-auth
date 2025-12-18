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
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenJwtService tokenJwtService;
  private final RedisService redisService;
  private final UserMapper userMapper;

  @Autowired
  public RefreshTokenUseCase(PasswordEncoder passwordEncoder, TokenJwtService tokenJwtService, RedisService redisService, UserMapper userMapper, UserRepository userRepository,  RefreshTokenRepository refreshTokenRepository) {
    this.passwordEncoder = passwordEncoder;
    this.tokenJwtService = tokenJwtService;
    this.redisService = redisService;
    this.userMapper = userMapper;
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
  }

//  @Transactional
//  public TokenResponse execute(String rawRefreshToken, String accessToken, String ipAddress, String userAgent) {
//
//  }
}
