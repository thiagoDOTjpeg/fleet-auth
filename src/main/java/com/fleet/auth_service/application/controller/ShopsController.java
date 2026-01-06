package com.fleet.auth_service.application.controller;


import com.fleet.auth_service.application.dto.request.metadata.ShopOwnerMetadata;
import com.fleet.auth_service.application.service.ShopsService;
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
@RequestMapping("/api/shops")
public class ShopsController {
  private final ShopsService shopsService;

  @Autowired
  public ShopsController(ShopsService shopsService) {
    this.shopsService = shopsService;
  }

  @PostMapping(value = "/onboarding", version = "1.0", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CLIENT')")
  public ResponseEntity<Void> onboardShop(
          @AuthenticationPrincipal User user,
          @RequestBody @Valid ShopOwnerMetadata metadata
  ) {
    shopsService.onboardShop(user, metadata);
    return ResponseEntity.ok().build();
  }
}
