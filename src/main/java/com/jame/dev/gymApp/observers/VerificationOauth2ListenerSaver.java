package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.oauth2.model.AuthenticatedUser;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationOauth2ListenerSaver {

   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;

   @Async
   @EventListener
   public void saveOauthUser(final AuthenticatedUser user) {
      final boolean exists = verificationService.isVerified(user.email());
      if (exists) return;

      log.info("Verified Oauth2 User");
      final String token = tokenGeneratorService.generateToken();
      final VerificationEntity ignored = verificationService
              .save(user.id(), token);
      verificationService.verify(user.email(), token);
   }
}
