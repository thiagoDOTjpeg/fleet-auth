package com.fleet.auth_service.application.mapper;

import com.fleet.auth_service.application.dto.request.RegisterRequest;
import com.fleet.auth_service.application.dto.summary.UserSummary;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
  UserSummary toUserSummary(User user);

  @Mapping(target = "name", source = "name")
  User registerRequestToUser(RegisterRequest registerRequest);

  default java.util.Set<String> mapRoles(java.util.Set<com.fleet.auth_service.domain.model.Role> roles) {
    if (roles == null) return java.util.Collections.emptySet();
    return roles.stream()
            .map(Role::getName)
            .collect(java.util.stream.Collectors.toSet());
  }
}


