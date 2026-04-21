package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.model.listeners.VerifyOauthUserEvent;
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

   @Async("taskExecutor")
   @EventListener
   public void saveOauthUser(VerifyOauthUserEvent event) {
      final AuthenticatedUser user = event.user();
      final boolean verified = verificationService.isVerified(user.email());
      if (verified)
         throw new VerificationAttemptFailedException("User already verified.");

      final String token = tokenGeneratorService.generateToken();
      final VerificationEntity ignored = verificationService.save(
         user.id(),
         token
      );
      verificationService.verify(user.email(), token);
   }
}
