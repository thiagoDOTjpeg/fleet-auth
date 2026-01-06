package com.fleet.auth_service.application.dto.events;


import com.fleet.auth_service.application.dto.request.RegistrationMetadata;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UserOnboardEvent(
        UUID userId,
        String email,
        RegistrationMetadata metadata,
        Instant occurredAt
) {}