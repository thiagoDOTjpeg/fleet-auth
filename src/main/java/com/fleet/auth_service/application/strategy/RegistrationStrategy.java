package com.fleet.auth_service.application.strategy;

import com.fleet.auth_service.application.dto.request.RegisterRequest;
import com.fleet.auth_service.domain.model.User;

public interface RegistrationStrategy {
  void validateAndPublish(RegisterRequest request, User user);
}
