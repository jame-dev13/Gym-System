package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.exception.AccountNotFoundException;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.listeners.RecoverySender;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.AccountRecoveryService;
import com.jame.dev.gymApp.service.in.VerificationService;
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
   public void sendRecoveryMail(final RecoverySender recoverySender) {
      final String recipient = recoverySender.email();
      final String token = recoverySender.token();
      if(!accountRecoveryService.accountExists(recipient)){
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
