package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.auth.service.AuthService;
import com.jame.dev.gymApp.exception.VerificationTokenNotFoundException;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/verify")
@RequiredArgsConstructor
public class VerificationController {

   private final AuthService authService;

   @PatchMapping("/{email}")
   public ResponseEntity<VerificationDto> verifyAccount(@PathVariable("email") final String email,
                                                        @RequestBody final VerificationRequest request) {
      final VerificationDto verified = authService.verify(email, request.token())
              .orElseThrow(() -> new VerificationTokenNotFoundException("Can't retrieve Verification."));
      log.info(verified.toString());
      return ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(verified);
   }
}
