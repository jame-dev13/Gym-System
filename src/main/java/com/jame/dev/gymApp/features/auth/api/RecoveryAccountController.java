package com.jame.dev.gymApp.features.auth.api;

import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishRecovery;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryAccountRequest;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.auth.application.contract.recovery.AccountRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/accounts")
@RequiredArgsConstructor
@Validated
public class RecoveryAccountController {
   private final AccountRecoveryService accountRecoveryService;

   @PostMapping("/recover")
   @PublishRecovery
   public ResponseEntity<Void> requestRecovery(
           @RequestBody @Valid RecoveryRequest ignored) {
      return ResponseEntity.accepted().build();
   }

   @PostMapping("/activate")
   public ResponseEntity<Void> reActivateAccount(
           @RequestBody
           @Valid
           @NotNullObject final RecoveryAccountRequest recoveryAccountRequest
   ) {
      accountRecoveryService.reActivateUserAccount(
              recoveryAccountRequest.email(),
              recoveryAccountRequest.token()
      );
      return ResponseEntity.ok().build();
   }

}
