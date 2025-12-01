package com.fleet.auth_service.application.dto.response;

import com.fleet.auth_service.application.dto.summary.UserSummary;

public record TokenResponse(String accessToken, String refreshToken, UserSummary userSummary) {
}
