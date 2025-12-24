package com.fleet.auth_service.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.mapper.UserMapper;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.config.properties.JwtProperties;
import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.UnauthorizedException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenJwtService {
  private final JwtProperties jwtProperties;
  private Algorithm algorithm;

  public TokenJwtService(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  @PostConstruct
  protected void init() {
    this.algorithm = Algorithm.HMAC512(jwtProperties.getSecret());
  }

  public String generateAccessToken(User user) {
    String randomJWTid = UUID.randomUUID().toString();
    long accessExpMillis = jwtProperties.getExpiration().getAccessToken();
    return buildJwt(user, Instant.now(), accessExpMillis, randomJWTid);
  }
  public String generateRefreshToken(User user) {
    String randomJWTid = UUID.randomUUID().toString();
    long refreshExpMillis = jwtProperties.getExpiration().getRefreshToken();
    return buildJwt(user,  Instant.now(), refreshExpMillis, randomJWTid);
  }

  public DecodedJWT validateAndDecode(String token) {
    try {
      String cleanToken = token.replace("Bearer ", "");

      Algorithm algorithm = Algorithm.HMAC512(jwtProperties.getSecret());

      JWTVerifier verifier = JWT.require(algorithm)
              .withAudience(jwtProperties.getAudience())
              .withIssuer(jwtProperties.getIssuer())
              .build();

      return verifier.verify(cleanToken);
    } catch (JWTVerificationException exception) {
      throw new RuntimeException("Expired or invalid token", exception);
    }
  }

  public DecodedJWT decodeToken(String token) {
    try {
      String cleanToken = token.replace("Bearer ", "");
      return JWT.decode(cleanToken);
    } catch (JWTDecodeException exception) {
      throw new UnauthorizedException("Malformatted token");
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

  public static String hashToken(String rawToken) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error to calculate SHA-256 hash", e);
    }
  }

  private String buildJwt(User user, Instant now, long expirationMillis, String tokenJWTid) {
    return JWT.create()
            .withIssuer(jwtProperties.getIssuer())
            .withSubject(user.getId().toString())
            .withAudience(jwtProperties.getAudience())
            .withClaim("name", user.getName())
            .withClaim("email", user.getEmail())
            .withClaim("userType", user.getUserType().toString())
            .withArrayClaim("roles", user.getRoles().stream()
                    .map(Role::getName)
                    .toArray(String[]::new))
            .withJWTId(tokenJWTid)
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(expirationMillis))
            .sign(algorithm);
  }
}