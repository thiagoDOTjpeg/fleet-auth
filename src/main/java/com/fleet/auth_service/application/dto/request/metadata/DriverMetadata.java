package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DriverMetadata(
        @NotBlank(message = "CNH é obrigatória")
        @Pattern(
                regexp = "^\\d{11}$",
                message = "CNH deve conter exatamente 11 dígitos numéricos"
        )
        String cnh,

        @NotBlank(message = "Placa do veículo é obrigatória")
        @Pattern(
                regexp = "^[A-Z]{3}\\d{4}$|^[A-Z]{3}\\d[A-Z]\\d{2}$",
                message = "Placa deve estar no formato AAA9999 (antigo) ou AAA9A99 (Mercosul)"
        )
        String vehiclePlate,

        @NotBlank(message = "Tipo do veículo é obrigatório")
        @Size(min = 3, max = 50, message = "Tipo do veículo deve ter entre 3 e 50 caracteres")
        String vehicleType
) implements RegistrationMetadata {}