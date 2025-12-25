package com.fleet.auth_service.application.dto.request.metadata;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ShopOwnerMetadata(
        @NotBlank(message = "CNPJ é obrigatório")
        @Pattern(
                regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$|^\\d{14}$",
                message = "CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX ou conter 14 dígitos"
        )
        String cnpj,

        @NotBlank(message = "Endereço é obrigatório")
        @Size(min = 10, max = 200, message = "Endereço deve ter entre 10 e 200 caracteres")
        String address,

        @NotBlank(message = "Horário de funcionamento é obrigatório")
        @Pattern(
                regexp = "^([01]\\d|2[0-3]):[0-5]\\d-([01]\\d|2[0-3]):[0-5]\\d$",
                message = "Horário deve estar no formato HH:mm-HH:mm (ex: 08:00-18:00)"
        )
        String openingHours
) implements RegistrationMetadata {}