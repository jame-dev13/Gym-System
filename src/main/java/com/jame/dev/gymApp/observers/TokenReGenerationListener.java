package com.jame.dev.gymApp.observers;


import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.listeners.TokenGenerationEvent;
import com.jame.dev.gymApp.model.messages.EmailDetails;
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
public class TokenReGenerationListener {

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
