package com.fleet.auth_service.application.useCase.auth;

import com.fleet.auth_service.application.dto.request.RegisterRequest;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.domain.model.RefreshToken;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.domain.model.UserSession;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import com.fleet.auth_service.infra.repository.RefreshTokenRepository;
import com.fleet.auth_service.infra.repository.RoleRepository;
import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.ConflictException;
import com.fleet.auth_service.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegisterAuthUseCase {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final RedisService redisService;
  private final TokenJwtService tokenJwtService;
  private final UserMapper userMapper;

  @Autowired
  public RegisterAuthUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, RedisService redisService, TokenJwtService tokenJwtService, UserMapper userMapper,  RefreshTokenRepository refreshTokenRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.redisService = redisService;
    this.tokenJwtService = tokenJwtService;
    this.userMapper = userMapper;
  }

  @Transactional
  public TokenResponse execute(RegisterRequest  registerRequest, String ipAddress, String userAgent) {
    Optional<User> user = userRepository.findByEmail(registerRequest.email());
    if (user.isPresent()) throw new ConflictException("Email already in use");

    User userToRegister = userMapper.registerRequestToUser(registerRequest);
    Role clientRole = roleRepository.findRoleByName("ROLE_CLIENT")
      .orElseThrow(() -> new ResourceNotFoundException("Client role not found"));
    userToRegister.setPassword(passwordEncoder.encode(registerRequest.password()));
    userToRegister.addRole(clientRole);

    User savedUser = userRepository.save(userToRegister);

    String accessToken = tokenJwtService.generateAccessToken(savedUser);
    String refreshToken = tokenJwtService.generateRefreshToken(savedUser);
    String hashRefresh = TokenJwtService.hashToken(refreshToken);

    Instant issueTime = Instant.now();

    UserSession userSession = new UserSession(UUID.randomUUID(), ipAddress, userAgent, issueTime, savedUser.getId(), hashRefresh);
    refreshTokenRepository.save(new RefreshToken(savedUser, hashRefresh, null, userSession.getSessionId(), issueTime.plus(7, ChronoUnit.DAYS)));

    redisService.saveSession(savedUser.getId(), userSession, Duration.ofDays(7));

    return new TokenResponse(accessToken, refreshToken, userMapper.toUserSummary(savedUser));
  }
}
