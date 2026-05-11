package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.features.auth.domain.exception.AccountNotFoundException;
import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.auth.domain.event.RecoverySenderEvent;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.auth.application.contract.recovery.AccountRecoveryService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecoverySenderListener {
   private final AccountRecoveryService accountRecoveryService;
   private final EmailService emailService;
   private final VerificationService verificationService;

   @EventListener
   public void sendRecoveryMail(final RecoverySenderEvent recoverySenderEvent) {
      final String recipient = recoverySenderEvent.email();
      final String token = recoverySenderEvent.token();
      if (!accountRecoveryService.accountExists(recipient)) {
         throw new AccountNotFoundException("Account not found.");
      }

      verificationService.update(recipient, token);

      final var emailDetails = new EmailDetails(
              recipient,
              HtmlTemplates.recoveryTemplate(recipient, token),
              "Recovery.");

      emailService.sendSimpleEmail(emailDetails);
   }
}
