package com.fleet.auth_service.application.useCase.auth;

import com.fleet.auth_service.application.dto.request.RegisterRequest;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.application.strategy.RegistrationStrategy;
import com.fleet.auth_service.application.strategy.factory.RegistrationFactory;
import com.fleet.auth_service.domain.model.RefreshToken;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.domain.model.UserSession;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import com.fleet.auth_service.infra.repository.RefreshTokenRepository;
import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.ConflictException;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegisterAuthUseCase {
  private static final Logger log = LoggerFactory.getLogger(RegisterAuthUseCase.class);
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final RegistrationFactory registrationFactory;
  private final PasswordEncoder passwordEncoder;
  private final RedisService redisService;
  private final TokenJwtService tokenJwtService;
  private final UserMapper userMapper;

  @Autowired
  public RegisterAuthUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, RedisService redisService, TokenJwtService tokenJwtService, UserMapper userMapper,  RefreshTokenRepository refreshTokenRepository, RegistrationFactory registrationFactory) {
    this.userRepository = userRepository;
    this.registrationFactory = registrationFactory;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.redisService = redisService;
    this.tokenJwtService = tokenJwtService;
    this.userMapper = userMapper;
  }

  @Transactional
  public TokenResponse execute(RegisterRequest  registerRequest, String ipAddress, String userAgent) {
    Optional<User> user = userRepository.findByEmailAndUserType(registerRequest.email(), registerRequest.userType());
    if (user.isPresent()) throw new ConflictException("Email already in use");

    RegistrationStrategy strategy =  registrationFactory.getStrategy(registerRequest.userType());

    User userToRegister = userMapper.registerRequestToUser(registerRequest);
    userToRegister.setPassword(passwordEncoder.encode(registerRequest.password()));

    strategy.prepare(userToRegister, registerRequest.metadata());

    User savedUser = userRepository.save(userToRegister);


    String accessToken = tokenJwtService.generateAccessToken(savedUser);
    String refreshToken = tokenJwtService.generateRefreshToken(savedUser);
    String hashRefresh = TokenJwtService.hashToken(refreshToken);

    Instant issueTime = Instant.now();

    refreshTokenRepository.save(new RefreshToken(savedUser, hashRefresh, null, issueTime.plus(7, ChronoUnit.DAYS)));
    UserSession userSession = new UserSession(UUID.randomUUID(), ipAddress, userAgent, issueTime, savedUser.getId(), hashRefresh);

    redisService.saveSession(savedUser.getId(), userSession, Duration.ofDays(7));

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        strategy.execute(savedUser, registerRequest.metadata());
      }
    });
    return new TokenResponse(accessToken, refreshToken, userMapper.toUserSummary(savedUser));
  }
}
