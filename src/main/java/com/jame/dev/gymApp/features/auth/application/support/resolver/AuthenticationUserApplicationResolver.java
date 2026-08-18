package com.jame.dev.gymApp.features.auth.application.support.resolver;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.auth.AuthenticationUserResolver;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthenticationUserApplicationResolver implements AuthenticationUserResolver {
   @Override
   public Long resolveUserId(Authentication authentication) {
      final var principal = (AuthPrincipal) Objects.requireNonNull(authentication.getPrincipal(), "Authentication is undefined.");
      return principal.id();
   }
}
