package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.application.contract.TokenDBHasherService;
import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.support.factory.VerificationFactory;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.notification.domain.event.VerificationSenderEvent;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VerificationNotifySignUpUser {
   private final UserQueryRepository userQueryRepository;
   private final VerificationRepository verificationRepository;
   private final VerificationFactory verificationFactory;
   private final TokenGeneratorService tokenGeneratorService;
   private final TokenDBHasherService hasherService;
   private final ApplicationEventPublisher applicationEventPublisher;

   @EventListener(UserRegisteredEvent.class)
   @Async("taskExecutor")
   @Transactional
   public void createVerificationAndNotify(final UserRegisteredEvent event) {
      final String email = event.email();
      userQueryRepository.findByEmail(email)
         .ifPresentOrElse(userEntity -> {
            final String rawToken = tokenGeneratorService.generateToken();
            final VerificationEntity verificationEntity = verificationFactory.createVerification(userEntity, hasherService.hashToken(rawToken));
            verificationRepository.saveAndFlush(verificationEntity);
            applicationEventPublisher.publishEvent(new VerificationSenderEvent(email, rawToken));
         }, () -> {
            throw new UserNotFoundException("User Not Found, did you completed the register process?");
         });
   }
}
