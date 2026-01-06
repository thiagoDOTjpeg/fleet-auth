package com.fleet.auth_service.application.controller;

import com.fleet.auth_service.application.dto.request.metadata.DriverMetadata;
import com.fleet.auth_service.application.service.DriverService;
import com.fleet.auth_service.domain.model.User;
import com.fleet.auth_service.shared.exception.ExceptionMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/drivers")
@Tag(name = "Driver", description = "Endpoints for driver management and onboarding")
public class DriverController {

  private final DriverService driverService;

  @Autowired
  public DriverController(DriverService driverService) {
    this.driverService = driverService;
  }

  @Operation(summary = "Driver Onboarding", description = "Registers driver metadata during the onboarding process")
  @ApiResponse(responseCode = "200", description = "Driver onboarded successfully")
  @ApiResponse(responseCode = "400", description = "Invalid driver metadata",
          content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token",
          content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @ApiResponse(responseCode = "403", description = "Forbidden - requires CLIENT role",
          content = @Content(schema = @Schema(implementation = ExceptionMessage.class)))
  @SecurityRequirement(name = "Bearer Authentication")
  @PostMapping(value = "/onboarding", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CLIENT')")
  public ResponseEntity<Void> onboardDriver(
          @AuthenticationPrincipal User user,
          @RequestBody @Valid DriverMetadata metadata
  ) {
    driverService.onboardDriver(user, metadata);
    return ResponseEntity.ok().build();
  }

}
