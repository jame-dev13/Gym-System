package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.auth.service.AuthService;
import com.jame.dev.gymApp.model.dto.auth.IdentityDto;
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

   private final AuthService authService;

   @GetMapping
   public ResponseEntity<IdentityDto> getMe(
           @CookieValue(name = "access") @NonNull final String access,
           @NonNull Authentication authentication) {
      final IdentityDto authMe = authService.setUser(access, authentication);
      return ResponseEntity.ok(authMe);
   }
}
