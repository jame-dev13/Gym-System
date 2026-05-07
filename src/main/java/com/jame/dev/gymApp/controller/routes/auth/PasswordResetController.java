package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.aspects.annotations.aspects.PublishPasswordResetRequest;
import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;
import com.jame.dev.gymApp.model.dto.auth.TokenIdResetPasswordRequest;
import com.jame.dev.gymApp.model.dto.in.PasswordResetDtoInput;
import com.jame.dev.gymApp.model.dto.in.RecoveryRequest;
import com.jame.dev.gymApp.service.in.OneTimeTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
         .location(URI.create("http://localhost:5173/password-reset"))
         .build();
   }

   @PostMapping("/set-password")
   public ResponseEntity<Void> setNewPassword(
      @RequestBody
      @Valid final PasswordResetDtoInput passwordResetDtoInput
   ) {
      oneTimeTokenService.resetPassword(passwordResetDtoInput);
      return ResponseEntity.ok().build();
   }
}
