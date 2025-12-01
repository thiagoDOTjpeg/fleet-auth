package com.fleet.auth_service.infra.repository;

import com.fleet.auth_service.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByName(String name);
  Optional<User> findByEmail(String email);
}
