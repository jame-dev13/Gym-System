package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationSenderApplicationService implements VerificationSenderService {
   private final EmailService emailService;

   @Override
   public void sendVerificationEmail(String email, String token) {
      final EmailDetails emailDetails = new EmailDetails(
              email, HtmlTemplates.verificationTemplate(email, token), "Account Verification."
      );
      emailService.sendSimpleEmail(emailDetails).thenAccept(
              sent -> log.warn(sent ? "Email sent": "Email cannot be send.")
      );
   }
}
