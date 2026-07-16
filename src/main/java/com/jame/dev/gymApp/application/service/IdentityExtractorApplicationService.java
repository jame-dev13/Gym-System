package com.jame.dev.gymApp.application.service;

import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class IdentityExtractorApplicationService implements IdentityExtractorService {

   @Override
   public String extract(final Authentication authentication) {
      if (authentication.getPrincipal() instanceof CustomOAuth2User user) {
         return user.getUser().email();
      }
      return Optional.ofNullable((UserPrincipal) authentication.getPrincipal())
         .map(UserPrincipal::getUsername)
         .orElseThrow(() -> new AuthenticationNullException("No Authenticated user were found."));
   }
}
