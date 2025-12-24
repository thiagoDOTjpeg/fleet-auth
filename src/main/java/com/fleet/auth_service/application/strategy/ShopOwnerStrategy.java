package com.fleet.auth_service.application.strategy;

import com.fleet.auth_service.application.dto.events.UserRegisteredEvent;
import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ShopOwnerMetadata;
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
public class ShopOwnerStrategy implements RegistrationStrategy{

  private final UserEventPublisher eventPublisher;
  private final RoleRepository roleRepository;

  @Autowired
  public ShopOwnerStrategy(UserEventPublisher eventPublisher, RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void prepare(User user, RegistrationMetadata metadata) {
    Role shopOwnerRole = roleRepository.findRoleByName("ROLE_MERCHANT")
            .orElseThrow(() -> new ResourceNotFoundException("Role 'ROLE_MERCHANT' not found"));

    user.addRole(shopOwnerRole);

    if(!(metadata instanceof ShopOwnerMetadata)) throw new IllegalArgumentException("Invalid metadata for SHOP_OWNER");
  }

  @Override
  public void execute(User user, RegistrationMetadata metadata) {
    if(metadata instanceof ShopOwnerMetadata(String cnpj, String address, String openingHours)){
      Map<String, Object> eventMetadata = Map.of(
              "cnpj", cnpj,
              "address", address,
              "openingHours", openingHours
      );

      eventPublisher.publishUserRegistered(new UserRegisteredEvent(user.getId(), user.getEmail(), user.getUserType(), eventMetadata, Instant.now()));
    }
  }

  @Override
  public UserType getUserType() {
    return UserType.SHOP_OWNER;
  }
}
