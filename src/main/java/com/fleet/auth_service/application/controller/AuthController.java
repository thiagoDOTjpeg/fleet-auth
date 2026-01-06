package com.fleet.auth_service.application.controller;

import com.fleet.auth_service.application.dto.request.LoginRequest;
import com.fleet.auth_service.application.dto.request.RegisterRequest;
import com.fleet.auth_service.application.dto.response.TokenResponse;
import com.fleet.auth_service.application.useCase.auth.*;
import com.fleet.auth_service.shared.exception.ExceptionMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for login, registration and token management")
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  private final LoginUseCase loginUseCase;
  private final LogoutUseCase logoutUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;
  private final RegisterAuthUseCase registerAuthUseCase;
  private final ValidateUseCase validateUseCase;

  @Autowired
  public AuthController(LoginUseCase loginUseCase, LogoutUseCase logoutUseCase, RefreshTokenUseCase refreshTokenUseCase, RegisterAuthUseCase registerAuthUseCase, ValidateUseCase validateUseCase) {
    this.loginUseCase = loginUseCase;
    this.logoutUseCase = logoutUseCase;
    this.refreshTokenUseCase = refreshTokenUseCase;
    this.registerAuthUseCase = registerAuthUseCase;
    this.validateUseCase = validateUseCase;
  }

  @Operation(summary = "Validate Token", description = "Verifies if the Access Token provided in the header is valid.")
  @ApiResponse(responseCode = "200", description = "Valid token")
  @ApiResponse(responseCode = "401", description = "Invalid or expired token", content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @GetMapping(value = "/validate", version = "1.0", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> validate(@RequestHeader("Authorization") String bearerToken) {
    validateUseCase.execute(bearerToken);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Logout", description = "Revokes the Refresh Token and invalidates the session.")
  @ApiResponse(responseCode = "204", description = "Logout successful")
  @PostMapping(value = "/logout", version = "1.0")
  public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                     @RequestHeader(value = "Authorization", required = false) String bearerToken) {
    if (refreshToken != null) {
      logoutUseCase.execute(refreshToken, bearerToken);
    }

    ResponseCookie cleanCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(false)
            .path("/api/auth/")
            .maxAge(0)
            .sameSite("Strict")
            .build();

    return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
            .build();
  }

  @Operation(summary = "Refresh Token", description = "Generates a new Access Token using a valid Refresh Token via Cookie.")
  @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
  @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @PostMapping(value = "/refresh", version = "1.0", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TokenResponse> refresh(
          @CookieValue(value = "refresh_token") String refreshToken,
          @RequestHeader(value = "User-Agent") String userAgent,
          HttpServletRequest request) {
    String ipAddress = extractClientIp(request);
    TokenResponse token = refreshTokenUseCase.execute(refreshToken, ipAddress, userAgent);

    ResponseCookie cookie = ResponseCookie.from("refresh_token", token.refreshToken())
            .httpOnly(true)
            .secure(false)
            .path("/api/auth/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new TokenResponse(token.accessToken(), null, token.userSummary()));
  }

  @Operation(summary = "User Registration", description = "Creates a new user account (Client, Driver, etc).")
  @ApiResponse(responseCode = "200", description = "User registered successfully")
  @ApiResponse(responseCode = "400", description = "Invalid data or validation error", content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @PostMapping(value = "/register",version = "1.0",  produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegisterRequest registerRequest,
                                                @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                                HttpServletRequest request) {
    String ipAddress = extractClientIp(request);

    TokenResponse token = registerAuthUseCase.execute(registerRequest, ipAddress, userAgent);

    ResponseCookie cookie = ResponseCookie.from("refresh_token", token.refreshToken())
            .httpOnly(true)
            .secure(false) // HTTPS
            .path("/api/auth/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Strict")
            .build();

    TokenResponse responseBody = new TokenResponse(token.accessToken(), null, token.userSummary());

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(responseBody);
  }

  @Operation(summary = "User Login", description = "Authenticates a user and returns access and refresh tokens.")
  @ApiResponse(responseCode = "200", description = "Login successful",
          content = @Content(schema = @Schema(implementation = TokenResponse.class)))
  @ApiResponse(responseCode = "401", description = "Invalid credentials",
          content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @PostMapping(value = "/login", version = "1.0", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                             @RequestHeader(value = "User-Agent") String userAgent,
                                             HttpServletRequest request) {
    String ipAddress = extractClientIp(request);

    TokenResponse token = loginUseCase.execute(loginRequest, ipAddress, userAgent);

    ResponseCookie cookie = ResponseCookie.from("refresh_token", token.refreshToken())
            .httpOnly(true)
            .secure(false) // HTTPS
            .path("/api/auth/")
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
