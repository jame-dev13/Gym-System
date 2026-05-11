package com.jame.dev.gymApp.features.auth.api;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.features.auth.application.contract.expiration.ExpirationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/expiration")
@RequiredArgsConstructor
public class ExpirationController {
   private final ExpirationService expirationService;

   @PatchMapping("/{email}/refresh")
   public ResponseEntity<Void> refresh(
           @PathVariable @Valid @EmailValid final String email
   ) {
      expirationService.getMoreTimeFor(email);
      return ResponseEntity.ok().build();
   }
}
