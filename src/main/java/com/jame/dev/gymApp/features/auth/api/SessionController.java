package com.jame.dev.gymApp.features.auth.api;

import com.jame.dev.gymApp.features.auth.api.response.SessionResponse;
import com.jame.dev.gymApp.features.auth.application.contract.session.SessionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
public class SessionController {

   private final SessionService sessionService;

   @GetMapping
   public ResponseEntity<SessionResponse> getMe(
           @Valid
           @CookieValue(name = "access") final String access,
           @NonNull Authentication authentication) {
      final SessionResponse sessionResponse = sessionService.getSession(access, authentication);
      return ResponseEntity.ok(sessionResponse);
   }
}
