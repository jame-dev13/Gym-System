package com.jame.dev.gymApp.features.auth.api;

import com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishPasswordResetRequest;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import com.jame.dev.gymApp.features.auth.api.request.TokenIdResetPasswordRequest;
import com.jame.dev.gymApp.features.auth.api.request.PasswordResetRequest;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.auth.application.contract.OneTimeTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static jakarta.servlet.http.HttpServletResponse.SC_FOUND;

@RequestMapping("/auth/passwords")
@RestController
@RequiredArgsConstructor
@Validated
public class PasswordResetController {
   @Value("${app.auth.redirect.url.password-reset:}")
   private String REDIRECT_URL;

   private final OneTimeTokenService oneTimeTokenService;

   @PostMapping("/request-reset")
   @PublishPasswordResetRequest
   public ResponseEntity<Void> requestReset(
      @RequestBody @Valid RecoveryRequest ignored
   ) {
      return ResponseEntity.accepted().build();
   }

   @GetMapping("/reset")
   public ResponseEntity<Void> resetPassword(
      @Valid
      @RequestParam("token")
      @NotEmptyNull String token,
      @RequestParam("uid")
      @Minimum long uid
   ) {
      oneTimeTokenService.validateTokenRequest(
         new TokenIdResetPasswordRequest(
            token,
            uid
         )
      );
      return ResponseEntity.status(SC_FOUND)
         .location(URI.create(REDIRECT_URL))
         .build();
   }

   @PostMapping("/set-password")
   public ResponseEntity<Void> setNewPassword(
      @RequestBody
      @Valid final PasswordResetRequest passwordResetRequest
   ) {
      oneTimeTokenService.resetPassword(passwordResetRequest);
      return ResponseEntity.ok().build();
   }
}
