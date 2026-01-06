package com.fleet.auth_service.domain.service;

import com.fleet.auth_service.infra.repository.UserRepository;
import com.fleet.auth_service.shared.exception.UnauthorizedException;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @NullMarked
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("Email or password is invalid/wrong"));
  }
}
