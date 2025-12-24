package com.fleet.auth_service.application.useCase.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fleet.auth_service.domain.service.RedisService;
import com.fleet.auth_service.domain.service.TokenJwtService;
import com.fleet.auth_service.shared.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ValidateUseCase {

  private final TokenJwtService tokenJwtService;
  private final RedisService redisService;

  @Autowired
  public ValidateUseCase(TokenJwtService tokenJwtService, RedisService redisService) {
    this.tokenJwtService = tokenJwtService;
    this.redisService = redisService;
  }

  public void execute(String bearerToken) {
    if(!bearerToken.startsWith("Bearer ")) throw new UnauthorizedException("Invalid/expired Token");

    String accessToken = bearerToken.substring(7);
    DecodedJWT decodedAccess;
    try {
      decodedAccess = tokenJwtService.validateAndDecode(accessToken);
    } catch (Exception e) {
      throw new UnauthorizedException("Invalid/expired Token");
    }

    if(redisService.isBlacklistedJti(UUID.fromString(decodedAccess.getSubject()), UUID.fromString(decodedAccess.getId()))) {
      throw new UnauthorizedException("Invalid/expired Token");
    }
  }
}
