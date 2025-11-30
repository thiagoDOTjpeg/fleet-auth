package com.fleet.auth_service.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fleet.auth_service.application.dto.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.config.properties.JwtProperties;
import com.fleet.auth_service.infra.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenJwtService {

  private final UserRepository userRepository;
  private final JwtProperties jwtProperties;
  private final UserMapper userMapper;
  private Algorithm algorithm;

  public TokenJwtService(UserRepository userRepository, JwtProperties jwtProperties, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.jwtProperties = jwtProperties;
    this.userMapper = userMapper;
  }

  @PostConstruct
  protected void init() {
    this.algorithm = Algorithm.HMAC512(jwtProperties.getSecret());
  }

  public TokenResponse createToken(User user) {
    return generateTokenResponse(user);
  }

  public TokenResponse refreshToken(String refreshToken) {
    try {
      DecodedJWT jwt = validateAndDecode(refreshToken);

      String userId = jwt.getSubject();

      User user = userRepository.findById(UUID.fromString(userId))
              .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o token fornecido"));

      if (!user.getActive()) {
        throw new RuntimeException("Usuário inativo. Realize login novamente.");
      }

      return generateTokenResponse(user);

    } catch (JWTVerificationException e) {
      throw new RuntimeException("Refresh token inválido ou expirado", e);
    }
  }

  public DecodedJWT validateAndDecode(String token) {
    try {
      String cleanToken = token.replace("Bearer ", "");

      Algorithm algorithm = Algorithm.HMAC512(jwtProperties.getSecret());

      JWTVerifier verifier = JWT.require(algorithm)
              .withIssuer(jwtProperties.getIssuer())
              .build();

      return verifier.verify(cleanToken);
    } catch (JWTVerificationException exception) {
      throw new RuntimeException("Token inválido ou expirado", exception);
    }
  }

  public Authentication getAuthentication(DecodedJWT decodedJWT) {
    var rolesClaim = decodedJWT.getClaim("roles");
    List<String> roles = rolesClaim.isNull() ? Collections.emptyList() : rolesClaim.asList(String.class);

    List<GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
            .collect(Collectors.toList());

    String userId = decodedJWT.getSubject();

    return new UsernamePasswordAuthenticationToken(userId, null, authorities);
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(jwtProperties.getHeader());
    if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring("Bearer ".length());
    }
    return null;
  }


  private TokenResponse generateTokenResponse(User user) {
    Instant now = Instant.now();

    long accessExpMillis = jwtProperties.getExpiration().getAccessToken();
    long refreshExpMillis = jwtProperties.getExpiration().getRefreshToken();

    String accessToken = buildJwt(user, now, accessExpMillis);
    String refreshToken = buildJwt(user, now, refreshExpMillis);

    long expiresInSeconds = accessExpMillis / 1000;

    return new TokenResponse(
            accessToken,
            refreshToken,
            Instant.now().plusSeconds(expiresInSeconds),
            "Bearer",
            userMapper.toUserSummary(user)
    );
  }

  private String buildJwt(User user, Instant now, long expirationMillis) {
    return JWT.create()
            .withIssuer(jwtProperties.getIssuer())
            .withSubject(user.getId().toString())
            .withAudience(jwtProperties.getAudience())
            .withClaim("name", user.getName())
            .withClaim("email", user.getEmail())
            .withArrayClaim("roles", new String[]{user.getUserType().toString()})
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(expirationMillis))
            .sign(algorithm);
  }
}