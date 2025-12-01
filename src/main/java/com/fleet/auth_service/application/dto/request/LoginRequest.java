package com.fleet.auth_service.application.dto.request;

import com.fleet.auth_service.domain.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "O email não pode estar em branco")
        @Email(message = "Deve ser um endereço de email válido")
        @Size(max = 255, message = "O email não pode ter mais de 255 caracteres")
        String email,

        @NotBlank(message = "A senha não pode estar em branco")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
        String password,

        @NotNull(message = "O tipo de usuário é obrigatório")
        UserType type
) {
}