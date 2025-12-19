package com.fleet.auth_service.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.application.dto.request.metadata.DriverMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ShopMetadata;
import com.fleet.auth_service.domain.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be at most 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
        )
        String password,
        UserType userType,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "userType"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = DriverMetadata.class, name = "DRIVER"),
                @JsonSubTypes.Type(value = ShopMetadata.class, name = "SHOP_OWNER"),
                @JsonSubTypes.Type(value = ClientMetadata.class, name = "CLIENT"),
                @JsonSubTypes.Type(value = ClientMetadata.class, name = "ADMIN")
        })
        RegistrationMetadata metadata
) {}