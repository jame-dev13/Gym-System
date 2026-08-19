package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.auth.domain.exception.InvalidAuthenticationPrincipalException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class IdentityExtractorApplicationService implements IdentityExtractorService {

   @Override
   public String extract(final Authentication authentication) {
      final Object principal = authentication.getPrincipal();

      if (!(principal instanceof AuthPrincipal authToken))
         throw new InvalidAuthenticationPrincipalException("Authentication principal not valid.");

      return authToken.username();
   }

   @Override
   public AuthPrincipal getContextPrincipal() {
      final Object principal = Objects
         .requireNonNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication undefined.")
         .getPrincipal();

      if (!(principal instanceof AuthPrincipal authToken))
         throw new InvalidAuthenticationPrincipalException("Authentication principal not valid.");

      return authToken;
   }

   @Override
   public CustomOAuth2User getOauthUser(Authentication authentication) {
      final Object principal = authentication.getPrincipal();

      if (!(principal instanceof CustomOAuth2User oauth))
         throw new InvalidAuthenticationPrincipalException("Principal isn't instance of Oauth.");

      return oauth;
   }

   @Override
   public UserPrincipal getUserPrincipal(Authentication authentication) {
      final Object principal = authentication.getPrincipal();

      if (!(principal instanceof UserPrincipal user))
         throw new InvalidAuthenticationPrincipalException("Principal isn't a local Authentication instance.");

      return user;
   }
}
