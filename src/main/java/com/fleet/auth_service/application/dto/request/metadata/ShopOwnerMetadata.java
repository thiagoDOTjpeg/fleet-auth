package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Shop owner-specific metadata")
public record ShopOwnerMetadata(
        @Schema(description = "Shop's CNPJ (Brazilian corporate tax ID)", example = "12.345.678/0001-90", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "CNPJ is required")
        @Pattern(
                regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$|^\\d{14}$",
                message = "CNPJ must be in format XX.XXX.XXX/XXXX-XX or contain 14 digits"
        )
        String cnpj,

        @Schema(description = "Shop's physical address", example = "123 Main Street, Downtown", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Address is required")
        @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
        String address,

        @Schema(description = "Shop's opening hours", example = "08:00-18:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Opening hours is required")
        @Pattern(
                regexp = "^([01]\\d|2[0-3]):[0-5]\\d-([01]\\d|2[0-3]):[0-5]\\d$",
                message = "Opening hours must be in format HH:mm-HH:mm (e.g., 08:00-18:00)"
        )
        String openingHours
) implements RegistrationMetadata {}
