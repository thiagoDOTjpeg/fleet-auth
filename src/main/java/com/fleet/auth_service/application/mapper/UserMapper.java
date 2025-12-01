package com.fleet.auth_service.application.mapper;

import com.fleet.auth_service.application.dto.summary.UserSummary;
import com.fleet.auth_service.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "role", source = "userType")
  UserSummary toUserSummary(User user);
}
