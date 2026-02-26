package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import com.jame.dev.gymApp.service.in.IdentityExtractorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class IdentityExtractorServiceImplementation implements IdentityExtractorService {

   @Override
   public String extract(final Authentication authentication) {
      return (authentication.getPrincipal() instanceof CustomOAuth2User user) ?
              user.getUser().email() : authentication.getName();
   }
}
