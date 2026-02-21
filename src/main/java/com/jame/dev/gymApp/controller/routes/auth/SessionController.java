package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.model.dto.auth.SessionDto;
import com.jame.dev.gymApp.service.in.SessionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

   private final SessionService sessionService;

   @GetMapping
   public ResponseEntity<SessionDto> getMe(
           @CookieValue(name = "access") @NonNull final String access,
           @NonNull Authentication authentication) {
      final SessionDto sessionDto = sessionService.getSession(access, authentication);
      return ResponseEntity.ok(sessionDto);
   }
}
