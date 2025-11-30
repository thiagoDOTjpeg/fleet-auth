package com.fleet.auth_service.application.dto;

import java.time.Instant;

public record TokenResponse(String accessToken, String refreshToken, Instant expiresIn, String tokenType, UserSummary userSummary) {
}
