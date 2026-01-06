package com.fleet.auth_service.application.dto.events;


import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        Map<String, Object> metadata,
        Instant occurredAt
) {}