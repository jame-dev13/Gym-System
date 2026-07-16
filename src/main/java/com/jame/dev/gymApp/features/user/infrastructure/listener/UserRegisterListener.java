package com.jame.dev.gymApp.features.user.infrastructure.listener;

import com.jame.dev.gymApp.infrastructure.security.token.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.auth.domain.event.UserNotifiableEvent;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.event.EmailDetailsEvent;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterListener {
   private final UserQueryRepository userQueryRepository;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;
   private final ApplicationEventPublisher applicationEventPublisher;

   @EventListener
   @Async("taskExecutor")
   public void verifyAndNotify(final UserNotifiableEvent event) {
      final String email = event.email();
      userQueryRepository.findByEmail(email)
         .ifPresentOrElse(
            user -> {
               final String rawToken = tokenGeneratorService.generateToken();
               final VerificationEntity verificationSaved = verificationService.save(user, rawToken);
               verificationService.verify(verificationSaved, rawToken);
               final var emailDetails = EmailDetails.builder()
                  .subject("Welcome")
                  .msgBody(HtmlTemplates.adminTemplate(
                     email,
                     event.rawPassword()))
                  .recipient(email)
                  .build();
               applicationEventPublisher.publishEvent(new EmailDetailsEvent(emailDetails));
            },
            () -> {
               throw new UserEntityNotFoundException("User not found.");
            });
   }
}
