package com.fleet.auth_service.application.strategy;

import com.fleet.auth_service.application.dto.events.UserRegisteredEvent;
import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.application.ports.output.UserEventPublisher;
import com.fleet.auth_service.domain.enums.UserType;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.repository.RoleRepository;
import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;


@Component
public class ClientStrategy implements RegistrationStrategy {

  private final UserEventPublisher eventPublisher;
  private final RoleRepository roleRepository;

  @Autowired
  public ClientStrategy(UserEventPublisher eventPublisher, RoleRepository roleRepository) {
    this.eventPublisher = eventPublisher;
    this.roleRepository = roleRepository;
  }

  @Override
  public void prepare(User user, RegistrationMetadata metadata) {
    Role clientRole = roleRepository.findRoleByName("ROLE_USER")
            .orElseThrow(() -> new ResourceNotFoundException("Role 'ROLE_USER' not found"));
    user.addRole(clientRole);

    if(!(metadata instanceof ClientMetadata)) throw new IllegalArgumentException("Invalid metadata for CLIENT");
  }

  @Override
  public void execute(User user, RegistrationMetadata metadata) {
    if(metadata instanceof ClientMetadata(String cpf)) {
      Map<String, Object> eventMetadata = Map.of(
              "cpf", cpf
      );

      eventPublisher.publishUserRegistered(new UserRegisteredEvent(
              user.getId(),
              user.getEmail(),
              user.getUserType(),
              eventMetadata,
              Instant.now()));
    } else {
      throw new IllegalArgumentException("Invalid metadata for CLIENT");
    }
  }

  @Override
  public UserType getUserType() {
    return UserType.CLIENT;
  }
}
