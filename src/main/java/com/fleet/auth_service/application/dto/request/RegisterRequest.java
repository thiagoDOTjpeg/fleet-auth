package com.fleet.auth_service.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fleet.auth_service.application.dto.request.metadata.AdminMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.application.dto.request.metadata.DriverMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ShopOwnerMetadata;
import com.fleet.auth_service.domain.enums.UserType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be at most 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
//        @Pattern(
//                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
//                message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
//        )
        String password,

        @NotNull(message = "User type is required")
        UserType userType,

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