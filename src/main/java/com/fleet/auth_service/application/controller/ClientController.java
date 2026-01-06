package com.fleet.auth_service.application.controller;

import com.fleet.auth_service.application.dto.request.metadata.ClientMetadata;
import com.fleet.auth_service.application.service.ClientService;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.shared.exception.ExceptionMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Client", description = "Client management endpoints")
public class ClientController {
  private final ClientService clientService;

  @Autowired
  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping(value = "/onboarding", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CLIENT')")
  @Operation(
          summary = "Onboard a new client",
          description = "Register client metadata during onboarding process"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Client onboarded successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid client metadata", content = @Content(schema = @Schema(implementation = ExceptionMessage.class))),
          @ApiResponse(responseCode = "401", description = "Unauthorized" , content = @Content(schema = @Schema(implementation = ExceptionMessage.class))),
          @ApiResponse(responseCode = "403", description = "Forbidden - requires CLIENT role", content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  })
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<Void> onboardClient(
          @AuthenticationPrincipal User user,
          @RequestBody @Valid ClientMetadata metadata
  ) {
    clientService.onboardClient(user, metadata);
    return ResponseEntity.ok().build();
  }
}
