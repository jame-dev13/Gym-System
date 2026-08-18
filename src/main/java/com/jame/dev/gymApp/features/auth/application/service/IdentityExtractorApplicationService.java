package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.exception.InvalidAuthenticationPrincipalException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class IdentityExtractorApplicationService implements IdentityExtractorService {

   @Override
   public String extract(final Authentication authentication) {
      return Optional.ofNullable((AuthPrincipal) authentication.getPrincipal())
         .map(AuthPrincipal::username)
         .orElseThrow(AuthenticationNullException::new);
   }

   @Override
   public AuthPrincipal getContextPrincipal() {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (Objects.isNull(authentication))
         throw new AuthenticationNullException();

      return (AuthPrincipal) Optional.ofNullable(authentication.getPrincipal())
         .orElseThrow(InvalidAuthenticationPrincipalException::new);
   }

   @Override
   public CustomOAuth2User getOauthUser(Authentication authentication) {
      final AuthPrincipal principal = (AuthPrincipal) Optional.ofNullable(authentication.getPrincipal())
         .orElseThrow(AuthenticationNullException::new);

      if(!(principal instanceof CustomOAuth2User oauth))
         throw new InvalidAuthenticationPrincipalException("Principal isn't instance o Oauth.");

      return oauth;
   }

   @Override
   public UserPrincipal getUserPrincipal(Authentication authentication) {
      final AuthPrincipal principal = (AuthPrincipal) Optional.ofNullable(authentication.getPrincipal())
         .orElseThrow(AuthenticationNullException::new);

      if(!(principal instanceof UserPrincipal user))
         throw new InvalidAuthenticationPrincipalException("Principal isn't instance of Local Auth.");

      return user;
   }
}
