package com.fleet.auth_service.infra.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fleet.auth_service.domain.service.TokenJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenJwtService jwtService;

  public JwtAuthenticationFilter(TokenJwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  @NullMarked
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = jwtService.resolveToken(request);

    if (token != null) {
      try {
        DecodedJWT decodedJWT = jwtService.validateAndDecode(token);

        Authentication auth = jwtService.getAuthentication(decodedJWT);

        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (Exception e) {
        SecurityContextHolder.clearContext();

      }
    }
    filterChain.doFilter(request, response);
  }
}