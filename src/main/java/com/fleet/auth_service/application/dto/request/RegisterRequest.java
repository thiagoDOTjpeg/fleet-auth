package com.fleet.auth_service.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "User registration request")
public record RegisterRequest(
        @Schema(description = "User's full name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        @Schema(description = "User's email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be at most 255 characters")
        String email,

        @Schema(description = "User's password", example = "Password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String password) {}
