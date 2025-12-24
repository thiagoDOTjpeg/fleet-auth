package com.fleet.auth_service.domain.service;

import com.fleet.auth_service.domain.enums.UserType;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.infra.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @NullMarked
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String email = username;
    UserType type = null;

    if (username.contains(":")) {
      String[] parts = username.split(":");
      email = parts[0];
      try {
        type = UserType.valueOf(parts[1]);
      } catch (IllegalArgumentException e) {
        log.warn("Error to take userType");
      }
    }

    User user;
    if (type != null) {
      user = userRepository.findByEmailAndUserType(email, type)
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    } else {
      user = userRepository.findByEmail(email)
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    return user;
  }
}
