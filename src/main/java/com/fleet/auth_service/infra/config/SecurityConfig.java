package com.fleet.auth_service.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//  private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//  }

//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//            .csrf(csrf -> csrf.disable())
//            .sessionManagement(session ->
//                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
//                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
//                    .requestMatchers("/actuator/**").hasRole("ADMIN")
//                    .anyRequest().authenticated()
//            )
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//    return http.build();
//  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
          AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}