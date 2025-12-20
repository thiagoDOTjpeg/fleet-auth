package com.fleet.auth_service.application.dto.summary;


import com.fleet.auth_service.domain.model.Role;

import java.util.Set;
import java.util.UUID;

public record UserSummary(UUID id, String name, String email, Set<String> roles) {
}
