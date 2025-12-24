package com.fleet.auth_service.application.dto.events;

import com.fleet.auth_service.domain.enums.UserType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        UserType type,
        Map<String, Object> metadata,
        Instant occurredAt
) {}