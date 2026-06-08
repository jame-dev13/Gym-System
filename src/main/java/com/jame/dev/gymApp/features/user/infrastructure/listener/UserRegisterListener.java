package com.jame.dev.gymApp.features.user.infrastructure.listener;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.auth.domain.event.UserNotifiableEvent;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterListener {
   private final UserService userService;
   private final EmailService emailService;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;

   @EventListener
   @Async("taskExecutor")
   public void verifyAndNotify(final UserNotifiableEvent event) {
      final String email = event.email();
      userService.getUserByEmail(email)
         .ifPresentOrElse(
            user -> {
               final String rawToken = tokenGeneratorService.generateToken();
               final VerificationEntity verificationSaved = verificationService.save(user, rawToken);
               verificationService.verify(verificationSaved, rawToken);
               final var emailDetails = new EmailDetails(
                  email,
                  HtmlTemplates.adminTemplate(
                     email,
                     event.rawPassword()),
                  "Welcome."
               );
               emailService.sendSimpleEmail(emailDetails);

            },
            () -> {
               throw new UserEntityNotFoundException("User not found.");
            });
   }
}
