package com.jame.dev.gymApp.features.auth.application.support.resolver;

import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.auth.AuthenticationUserResolver;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthenticationUserApplicationResolver implements AuthenticationUserResolver {
   @Override
   public Long resolveUserId(Authentication authentication) {
      final var principal = Objects.requireNonNull(authentication.getPrincipal(), "Authentication is undefined.");
      return switch (principal) {
         case UserPrincipal local -> local.getId();
         case CustomOAuth2User oauth -> oauth.getUser().id();
         default -> throw new IllegalStateException("Unexpected value: " + authentication.getPrincipal());
      };
   }
}
