package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.features.auth.domain.event.VerifyOauthUserEvent;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
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
   public void saveAndVerifyOauthUser(VerifyOauthUserEvent event) {
      final AuthenticatedUser user = event.user();
      final boolean verified = verificationService.isVerified(user.email());
      if (verified)
         throw new VerificationAttemptFailedException("User already verified.");

      final UserEntity userEntity = verificationService
         .getByUserEmail(user.email())
         .getUser();

      final String token = tokenGeneratorService.generateToken();
      final VerificationEntity verification = verificationService.save(
         userEntity, token
      );

      verificationService.verify(verification, token);
   }
}
