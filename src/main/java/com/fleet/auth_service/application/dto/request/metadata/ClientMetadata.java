package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;

public record ClientMetadata(
        String cpf
) implements RegistrationMetadata {}
