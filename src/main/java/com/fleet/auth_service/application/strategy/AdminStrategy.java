package com.fleet.auth_service.application.strategy;

import com.fleet.auth_service.application.dto.request.RegistrationMetadata;
import com.fleet.auth_service.application.dto.request.metadata.AdminMetadata;
import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.domain.enums.UserType;
import com.fleet.auth_service.domain.model.Role;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.repository.RoleRepository;
import com.fleet.auth_service.shared.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminStrategy implements RegistrationStrategy{

  private final RoleRepository roleRepository;

  @Autowired
  public AdminStrategy(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public void prepare(User user, RegistrationMetadata metadata) {
    Role adminRole = roleRepository.findRoleByName("ROLE_ADMIN")
            .orElseThrow(() -> new ResourceNotFoundException("Role 'ROLE_ADMIN' not found"));
    user.addRole(adminRole);

    if(!(metadata instanceof AdminMetadata)) throw new IllegalArgumentException("Invalid metadata for ADMIN");
  }

  @Override
  public void execute(User user, RegistrationMetadata metadata) {

  }

  @Override
  public UserType getUserType() {
    return UserType.ADMIN;
  }
}
