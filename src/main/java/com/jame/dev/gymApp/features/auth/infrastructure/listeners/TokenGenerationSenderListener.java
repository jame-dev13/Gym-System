package com.jame.dev.gymApp.features.auth.infrastructure.listeners;


import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import com.jame.dev.gymApp.infrastructure.security.token.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.domain.event.TokenGenerationEvent;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.event.EmailDetailsEvent;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenGenerationSenderListener {

   private final TokenGeneratorService tokenGeneratorService;
   private final HashExecutor hasherService;
   private final VerificationRepository verificationRepository;
   private final ApplicationEventPublisher applicationEventPublisher;

   @EventListener
   @Async("taskExecutor")
   @Transactional
   public void tokenGeneratorSender(TokenGenerationEvent event) {
      final String email = event.email();
      if (verificationRepository.existsByUser_EmailAndVerifiedTrue(email)) {
         throw new IllegalArgumentException("This account is already verified.");
      }

      if (!verificationRepository.existsByUser_Email(email)) {
         throw new VerificationNotFoundException("Verification doesn't exist for the given email.");
      }

      final VerificationEntity verification = verificationRepository.findByUser_Email(email)
         .orElseThrow(() -> new VerificationNotFoundException("Verification not found for: " + email));

      final String rawToken = tokenGeneratorService.generateToken();
      verification.setToken(hasherService.hash(rawToken));
      verification.setExpiration(Instant.now().plus(10, ChronoUnit.MINUTES));
      verificationRepository.saveAndFlush(verification);

      final EmailDetails emailDetails = EmailDetails.builder()
         .recipient(email)
         .msgBody(HtmlTemplates.verificationTemplate(email, rawToken))
         .subject("New Verification Code")
         .build();

      applicationEventPublisher.publishEvent(new EmailDetailsEvent(emailDetails));
   }
}
