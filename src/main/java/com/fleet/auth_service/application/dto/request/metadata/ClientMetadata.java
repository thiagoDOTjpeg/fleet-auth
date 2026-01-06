package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Client-specific metadata")
public record ClientMetadata(
        @Schema(description = "Client's CPF (Brazilian tax ID)", example = "123.456.789-00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "CPF is required")
        @Pattern(
                regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$",
                message = "CPF must be in format XXX.XXX.XXX-XX or contain 11 digits"
        )
        String cpf
) implements RegistrationMetadata {}
