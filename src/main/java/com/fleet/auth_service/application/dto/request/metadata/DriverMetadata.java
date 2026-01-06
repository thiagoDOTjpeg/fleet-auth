package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Driver-specific metadata")
public record DriverMetadata(
        @Schema(description = "Driver's license number (CNH)", example = "12345678901", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "CNH is required")
        @Pattern(
                regexp = "^\\d{11}$",
                message = "CNH must contain exactly 11 numeric digits"
        )
        String cnh,

        @Schema(description = "Vehicle license plate", example = "ABC1234", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Vehicle plate is required")
        @Pattern(
                regexp = "^[A-Z]{3}\\d{4}$|^[A-Z]{3}\\d[A-Z]\\d{2}$",
                message = "Plate must be in format AAA9999 (old) or AAA9A99 (Mercosul)"
        )
        String vehiclePlate,

        @Schema(description = "Type of vehicle", example = "Motorcycle", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Vehicle type is required")
        @Size(min = 3, max = 50, message = "Vehicle type must be between 3 and 50 characters")
        String vehicleType
) implements RegistrationMetadata {}
