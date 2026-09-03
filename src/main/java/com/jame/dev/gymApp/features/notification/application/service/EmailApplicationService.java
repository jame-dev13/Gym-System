package com.jame.dev.gymApp.features.notification.application.service;

import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailApplicationService implements EmailService {
   private final JavaMailSender javaMailSender;

   @Value("${spring.mail.username}")
   private String sender;

   @Override
   @Async("mailExecutor")
   public CompletableFuture<Boolean> sendSimpleEmail(@NonNull EmailDetails emailDetails) {
      final MimeMessage message = javaMailSender.createMimeMessage();

      try {
         final MimeMessageHelper mime = new MimeMessageHelper(message, true, "UTF-8");
         message.setFrom(sender);
         mime.setTo(emailDetails.recipient());
         mime.setSubject(emailDetails.subject());
         mime.setText(emailDetails.msgBody(), true);
         javaMailSender.send(message);
         log.info("Mail message sent with success.");
         return CompletableFuture.completedFuture(true);
      } catch (MessagingException e) {
         log.error("Cannot sent mail message: ", e);
         return CompletableFuture.failedFuture(e);
      }
   }
}
