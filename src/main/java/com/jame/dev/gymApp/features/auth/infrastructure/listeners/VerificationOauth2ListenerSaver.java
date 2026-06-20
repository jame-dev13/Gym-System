package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.application.contract.TokenDBHasherService;
import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.support.factory.VerificationFactory;
import com.jame.dev.gymApp.features.auth.domain.event.VerifyOauthUserEvent;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationOauth2ListenerSaver {
   private final VerificationFactory verificationFactory;
   private final VerificationRepository verificationRepository;
   private final TokenDBHasherService hasherService;
   private final TokenGeneratorService tokenGeneratorService;
   private final UserQueryRepository userQueryRepository;

   @Async("taskExecutor")
   @EventListener(VerifyOauthUserEvent.class)
   @Transactional
   public void saveAndVerifyOauthUser(final VerifyOauthUserEvent event) {
      final AuthenticatedUser user = event.user();
      if (verificationRepository.existsByUser_EmailAndVerifiedTrue(user.email()))
         throw new VerificationAttemptFailedException("User already verified.");

      final UserEntity userEntity = userQueryRepository.findByEmail(user.email())
         .orElseThrow(() -> new UserEntityNotFoundException("User not found." + user.email()));

      final String token = tokenGeneratorService.generateToken();
      final VerificationEntity verification = verificationFactory.createVerification(
         userEntity, hasherService.hashToken(token));
      verification.setVerified(true);
      verificationRepository.saveAndFlush(verification);
   }
}
