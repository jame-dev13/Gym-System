package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.VerificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationSenderImplementation implements VerificationSenderService {
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
