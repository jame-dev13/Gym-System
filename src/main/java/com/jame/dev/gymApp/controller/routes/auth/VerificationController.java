package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
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
   public ResponseEntity<VerificationDto> verifyAccount(
           @PathVariable
           @EmailValid String email,
           @RequestBody
           @Valid
           @NotNullObject VerificationRequest request) {
      return ResponseEntity.ok()
              .body(verificationService.verify(email, request.token()));
   }

}
