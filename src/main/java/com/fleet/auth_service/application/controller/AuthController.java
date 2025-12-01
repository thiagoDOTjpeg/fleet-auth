package com.fleet.auth_service.application.controller;

import com.fleet.auth_service.application.dto.request.LoginRequest;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.useCase.auth.LoginUseCase;
import com.fleet.auth_service.application.useCase.auth.RefreshTokenUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

  private final LoginUseCase loginUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;

  @Autowired
  public AuthController(LoginUseCase loginUseCase, RefreshTokenUseCase refreshTokenUseCase) {
    this.loginUseCase = loginUseCase;
    this.refreshTokenUseCase = refreshTokenUseCase;
  }

  @PostMapping(value = "/login", version = "1.0", produces = MediaType.APPLICATION_JSON_VALUE,  consumes = MediaType.APPLICATION_JSON_VALUE)
  @NullMarked
  public ResponseEntity<TokenResponse> login(
          @RequestBody @Valid LoginRequest loginRequest,
          @RequestHeader(value = "User-Agent", required = false) String userAgent,
          HttpServletRequest request
  ) {
    String ipAddress = extractClientIp(request);

    TokenResponse token = loginUseCase.execute(loginRequest, ipAddress, userAgent);

    ResponseCookie cookie = ResponseCookie.from("refresh_token", token.refreshToken())
            .httpOnly(true)
            .secure(false) // HTTPS
            .path("/auth/refresh")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Strict")
            .build();

    TokenResponse responseBody = new TokenResponse(token.accessToken(), null, token.userSummary());

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(responseBody);
  }

  private String extractClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-FORWARDED-FOR");

    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      return request.getRemoteAddr();
    }

    if (ip.contains(",")) {
      return ip.split(",")[0].trim();
    }

    return ip;
  }
}