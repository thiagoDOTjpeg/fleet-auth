package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientMetadata(
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(
                regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$",
                message = "CPF deve estar no formato XXX.XXX.XXX-XX ou conter 11 dígitos"
        )
        String cpf
) implements RegistrationMetadata {}
