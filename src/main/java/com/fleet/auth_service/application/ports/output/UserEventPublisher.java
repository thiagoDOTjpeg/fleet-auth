package com.fleet.auth_service.application.ports.output;

import com.fleet.auth_service.application.dto.events.UserRegisteredEvent;

public interface UserEventPublisher {
  void publishUserRegistered(UserRegisteredEvent event);
}
