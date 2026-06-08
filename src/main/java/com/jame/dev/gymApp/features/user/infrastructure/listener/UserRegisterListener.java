package com.jame.dev.gymApp.features.user.infrastructure.listener;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.auth.domain.event.UserNotifiableEvent;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterListener {
   private final EmailService emailService;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;

   @EventListener
   @Async("taskExecutor")
   public void verifyAndNotify(final UserNotifiableEvent user) {
      if (!user.isNotifiable()) return;
      final VerificationEntity verificationEntity = verificationService.getByUserEmail(user.input().email());
      final UserEntity userEntity = verificationEntity.getUser();
      final String rawToken = tokenGeneratorService.generateToken();
      final VerificationEntity verificationSaved = verificationService.save(userEntity, rawToken);
      verificationService.verify(verificationSaved, rawToken);
      final String email = userEntity.getEmail();
      final var emailDetails = new EmailDetails(
         email,
         HtmlTemplates.adminTemplate(
            email,
            user.input().password()),
         "Welcome."
      );
      emailService.sendSimpleEmail(emailDetails);
   }
}
