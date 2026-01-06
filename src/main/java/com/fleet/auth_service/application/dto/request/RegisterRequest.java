package com.fleet.auth_service.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fleet.auth_service.application.dto.request.metadata.AdminMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.application.dto.request.metadata.DriverMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ShopOwnerMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
        String password,

        @Schema(
                description = "User type specific metadata",
                oneOf = {DriverMetadata.class, ShopOwnerMetadata.class, ClientMetadata.class, AdminMetadata.class},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "userType"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = DriverMetadata.class, name = "DRIVER"),
                @JsonSubTypes.Type(value = ShopOwnerMetadata.class, name = "SHOP_OWNER"),
                @JsonSubTypes.Type(value = ClientMetadata.class, name = "CLIENT"),
                @JsonSubTypes.Type(value = AdminMetadata.class, name = "ADMIN")
        })
        @NotNull(message = "Metadata is required")
        @Valid
        RegistrationMetadata metadata
) {}
