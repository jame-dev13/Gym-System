package com.jame.dev.gymApp.application.service;

import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.application.contract.IdentityExtractorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class IdentityExtractorApplicationService implements IdentityExtractorService {

   @Override
   public String extract(final Authentication authentication) {
      return (authentication.getPrincipal() instanceof CustomOAuth2User user) ?
              user.getUser().email() : authentication.getName();
   }
}
