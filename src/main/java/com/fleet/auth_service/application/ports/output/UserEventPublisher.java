package com.fleet.auth_service.application.ports.output;

import com.fleet.auth_service.application.dto.events.UserOnboardEvent;

public interface UserEventPublisher {
  void publishUserOnboard(UserOnboardEvent event);
}
