package com.fleet.auth_service.application.strategy;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import com.fleet.auth_service.domain.enums.UserType;
import com.fleet.auth_service.domain.model.User;

public interface RegistrationStrategy {
  void prepare(User user, RegistrationMetadata metadata);
  void execute(User user, RegistrationMetadata metadata);
  UserType getUserType();
}
