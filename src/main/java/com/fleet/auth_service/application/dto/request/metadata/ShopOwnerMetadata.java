package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;

public record ShopOwnerMetadata(
        String cnpj,
        String address,
        String openingHours
) implements RegistrationMetadata {}