package com.fleet.auth_service.application.service;

import com.fleet.auth_service.application.dto.events.UserOnboardEvent;
import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.messaging.publisher.OutboxUserEventPublisher;
import com.fleet.auth_service.infra.repository.RoleRepository;
import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ClientService {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final OutboxUserEventPublisher eventPublisher;

  @Autowired
  public ClientService(RoleRepository roleRepository, UserRepository userRepository, OutboxUserEventPublisher eventPublisher) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public void onboardClient(User user, ClientMetadata metadata) {
    Role clientRole = roleRepository.findRoleByName("ROLE_CLIENT")
            .orElseThrow(() -> new ResourceNotFoundException("ROLE_CLIENT role not found"));

    user.addRole(clientRole);
    userRepository.save(user);

    eventPublisher.publishUserOnboard(new UserOnboardEvent(user.getId(), user.getEmail(), metadata, Instant.now()));
  }
}
