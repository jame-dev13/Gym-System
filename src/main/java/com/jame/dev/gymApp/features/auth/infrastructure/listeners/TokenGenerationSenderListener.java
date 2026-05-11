package com.jame.dev.gymApp.features.auth.infrastructure.listeners;


import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.auth.domain.event.TokenGenerationEvent;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenGenerationSenderListener {

   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;
   private final EmailService emailService;

   @EventListener
   @Async("taskExecutor")
   public void tokenGeneratorSender(TokenGenerationEvent event) {
      final String email = event.email();
      if (verificationService.isVerified(email)) {
         throw new IllegalArgumentException("This account is already verified.");
      }

      if (verificationService.verificationExistsFor(email)) {
         final String rawToken = tokenGeneratorService.generateToken();
         verificationService.update(email, rawToken);

         final EmailDetails emailDetails = new EmailDetails(
            email,
            HtmlTemplates.verificationTemplate(email, rawToken),
            "New Verification Code"
         );
         emailService.sendSimpleEmail(emailDetails)
            .thenAccept(
               sent -> log.info(sent ? "Email sent" : "Cannot send email."));
      }
   }
}
