package com.jame.dev.gymApp.features.auth.api;

import com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishTokenGeneration;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.auth.api.request.VerificationRequest;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/verify")
@RequiredArgsConstructor
public class VerificationController {

   private final VerificationService verificationService;

   @PatchMapping("/{email}")
   public ResponseEntity<Void> verifyAccount(
           @PathVariable
           @EmailValid final String email,
           @RequestBody
           @Valid
           @NotNullObject final VerificationRequest request) {
      verificationService.verify(email, request.token());
      return ResponseEntity.ok().build();
   }

   @PatchMapping("/{email}/token")
   @PublishTokenGeneration
   public ResponseEntity<Void> reSendToken(
           @Valid
           @EmailValid
           @PathVariable final String email) {
      log.info("Re sending token.");
      return ResponseEntity.accepted().build();
   }
}
