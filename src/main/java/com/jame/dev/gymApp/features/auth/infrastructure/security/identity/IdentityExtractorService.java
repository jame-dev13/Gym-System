package com.jame.dev.gymApp.features.auth.infrastructure.security.identity;

import com.jame.dev.gymApp.features.auth.domain.exception.InvalidAuthenticationPrincipalException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import org.springframework.security.core.Authentication;

import java.util.Objects;

public interface IdentityExtractorService {
   String extract(final Authentication authentication);
   AuthPrincipal getContextPrincipal();
   CustomOAuth2User getOauthUser(Authentication authentication);
   UserPrincipal getUserPrincipal(Authentication authentication);

   default AuthPrincipal getAuthPrincipal(Authentication authentication) {
      final Object principal = Objects.requireNonNull(authentication, "Authentication is undefined.")
         .getPrincipal();
      if(!(principal instanceof AuthPrincipal auth)) {
         throw new InvalidAuthenticationPrincipalException("Auth Principal not valid.");
      }

      return auth;
   }
}
