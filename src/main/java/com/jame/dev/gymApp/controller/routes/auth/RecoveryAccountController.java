package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.aspects.annotations.PublishRecovery;
import com.jame.dev.gymApp.model.dto.in.RecoveryAccountDto;
import com.jame.dev.gymApp.model.dto.in.RecoveryRequest;
import com.jame.dev.gymApp.service.in.AccountRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
           @NotNullObject final RecoveryAccountDto recoveryAccountDto
   ) {
      accountRecoveryService.reActivateUserAccount(
              recoveryAccountDto.email(),
              recoveryAccountDto.token()
      );
      return ResponseEntity.ok().build();
   }

   @PostMapping("/activate-customer")
   public ResponseEntity<Void> reActivateCustomerAccount(
           @RequestBody
           @Valid
           @NotNullObject final RecoveryAccountDto recoveryAccountDto
   ) {
      accountRecoveryService.reactivateCustomerAccount(
              recoveryAccountDto.email(),
              recoveryAccountDto.token()
      );
      return ResponseEntity.ok().build();
   }
}
