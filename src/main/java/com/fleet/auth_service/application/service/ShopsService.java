package com.fleet.auth_service.application.service;

import com.fleet.auth_service.application.dto.events.UserOnboardEvent;
import com.fleet.auth_service.application.dto.request.metadata.ShopOwnerMetadata;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.messaging.publisher.OutboxUserEventPublisher;
import com.fleet.auth_service.infra.repository.RoleRepository;
import com.fleet.auth_service.infra.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ShopsService {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final OutboxUserEventPublisher eventPublisher;

  @Autowired
  public ShopsService(RoleRepository roleRepository, UserRepository userRepository, OutboxUserEventPublisher eventPublisher) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public void onboardShop(User user, ShopOwnerMetadata metadata) {
    Role shopOwnerRole = roleRepository.findRoleByName("ROLE_SHOP_OWNER")
            .orElseThrow(() -> new IllegalStateException("ROLE_SHOP_OWNER role not found"));

    user.addRole(shopOwnerRole);
    userRepository.save(user);

    eventPublisher.publishUserOnboard(new UserOnboardEvent(user.getId(), user.getEmail(), metadata, Instant.now()));
  }
}
