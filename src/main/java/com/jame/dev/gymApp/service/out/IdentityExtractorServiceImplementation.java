package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import com.jame.dev.gymApp.service.in.IdentityExtractorService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class IdentityExtractorServiceImplementation implements IdentityExtractorService {

   @Override
   public String extract(
           @NotNull(message = "No authentication provided")
           Authentication authentication) {
      return (authentication.getPrincipal() instanceof CustomOAuth2User user) ?
              user.getUser().email() : authentication.getName();
   }
}
