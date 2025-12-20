package com.fleet.auth_service.application.strategy;

import com.fleet.auth_service.application.dto.events.UserRegisteredEvent;
import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.application.dto.request.metadata.DriverMetadata;
import com.fleet.auth_service.application.ports.output.UserEventPublisher;
import com.fleet.auth_service.domain.enums.UserType;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.repository.RoleRepository;
import com.fleet.auth_service.shared.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class DriverStrategy implements RegistrationStrategy{

  private final UserEventPublisher eventPublisher;
  private final RoleRepository roleRepository;

  @Autowired
  public DriverStrategy(UserEventPublisher eventPublisher, RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void prepare(User user, RegistrationMetadata metadata) {
    Role driverRole = roleRepository.findRoleByName("ROLE_DRIVER")
            .orElseThrow(() -> new ResourceNotFoundException("Role 'ROLE_DRIVER' not found"));

    if(!(metadata instanceof ClientMetadata)) throw new IllegalArgumentException("Invalid metadata for DRIVER");
  }

  @Override
  public void execute(User user, RegistrationMetadata metadata) {
    if(metadata instanceof DriverMetadata(String cnh, String vehiclePlate, String vehicleType)) {
      Map<String, Object> eventMetadata = Map.of(
              "cnh", cnh,
              "vehiclePlate", vehiclePlate,
              "vehicleType", vehicleType
      );

      eventPublisher.publishUserRegistered(new UserRegisteredEvent(user.getId(), user.getEmail(), user.getUserType(), eventMetadata, Instant.now()));
    }
  }

  @Override
  public UserType getUserType() {
    return UserType.DRIVER;
  }
}
