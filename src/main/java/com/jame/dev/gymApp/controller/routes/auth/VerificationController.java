package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.aspects.annotations.aspects.PublishTokenGeneration;
import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.model.dto.auth.VerificationRequest;
import com.jame.dev.gymApp.service.in.VerificationService;
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
