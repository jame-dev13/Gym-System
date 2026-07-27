package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import com.jame.dev.gymApp.features.auth.domain.event.RecoverySenderEvent;
import com.jame.dev.gymApp.features.auth.domain.exception.AccountNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.event.EmailDetailsEvent;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class RecoverySenderListener {
   private final ApplicationEventPublisher applicationEventPublisher;
   private final VerificationRepository verificationRepository;
   private final HashExecutor hasherService;

   @EventListener
   @Async("taskExecutor")
   @Transactional
   public void sendRecoveryMail(final RecoverySenderEvent recoverySenderEvent) {
      final String recipient = recoverySenderEvent.email();
      final String token = recoverySenderEvent.token();
      if (!verificationRepository.existsDeactivatedByUser_Email(recipient)) {
         throw new AccountNotFoundException("Account not found.");
      }

      final VerificationEntity verification = verificationRepository.findDeactivatedByUser_Email(recipient)
         .orElseThrow(() -> new VerificationNotFoundException("Verification not found for: " + recipient));

      verification.setExpiration(Instant.now().plus(10, ChronoUnit.MINUTES));
      verification.setToken(hasherService.hash(token));
      verificationRepository.saveAndFlush(verification);

      final var emailDetails = EmailDetails.builder()
         .recipient(recipient)
         .msgBody(HtmlTemplates.recoveryTemplate(recipient, token))
         .subject("Recovery")
         .build();

      applicationEventPublisher.publishEvent(new EmailDetailsEvent(emailDetails));
   }
}
