package com.fleet.auth_service.application.controller;

import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.application.service.ClientService;
import com.fleet.auth_service.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
  private final ClientService clientService;

  @Autowired
  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping(value = "/onboarding", version = "1.0", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CLIENT')")
  public ResponseEntity<Void> onboardClient(
          @AuthenticationPrincipal User user,
          @RequestBody @Valid ClientMetadata metadata
  ) {
    clientService.onboardClient(user, metadata);
    return ResponseEntity.ok().build();
  }
}
