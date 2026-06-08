package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationSenderService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationNotifySignUpUser {
   private final UserService userService;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;
   private final VerificationSenderService verificationSenderService;

   @EventListener(UserRegisteredEvent.class)
   public void createVerificationAndNotify(final UserRegisteredEvent event) {
      final String email = event.email();
      userService.getUserByEmail(email)
         .ifPresentOrElse(userEntity -> {
            final String rawToken = tokenGeneratorService.generateToken();
            verificationService.save(userEntity, rawToken);
            verificationSenderService.sendVerificationEmail(email, rawToken);
         }, () -> {
            throw new UserNotFoundException("User Not Found, did you completed the register process?");
         });
   }
}
