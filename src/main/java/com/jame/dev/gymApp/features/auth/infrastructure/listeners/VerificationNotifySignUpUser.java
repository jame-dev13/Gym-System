package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationSenderService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import com.jame.dev.gymApp.features.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationNotifySignUpUser {
   private final UserRepository userRepository;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;
   private final VerificationSenderService verificationSenderService;

   @EventListener(UserRegisteredEvent.class)
   public void verifySignUpUser(final UserRegisteredEvent event) {
      userRepository.findByEmail(event.email())
         .ifPresentOrElse(
            user -> {
               final String rawToken = tokenGeneratorService.generateToken();
               verificationService.save(user.getId(), rawToken);
               verificationSenderService.sendVerificationEmail(user.getEmail(), rawToken);
            },
            () -> {
               throw new UserNotFoundException("User not found.");
            });
   }
}
