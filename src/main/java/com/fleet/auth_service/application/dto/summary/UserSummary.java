package com.fleet.auth_service.application.dto.summary;


import java.util.UUID;

public record UserSummary(UUID id, String name, String email, String role) {
}
