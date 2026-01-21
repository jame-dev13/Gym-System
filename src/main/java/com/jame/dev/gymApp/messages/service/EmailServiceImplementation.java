package com.jame.dev.gymApp.messages.service;

import com.jame.dev.gymApp.model.messages.EmailDetails;
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
public class EmailServiceImplementation implements EmailService {
   private final JavaMailSender javaMailSender;

   @Value("${spring.mail.username}")
   private String sender;

   @Override
   @Async
   public CompletableFuture<Boolean> sendSimpleEmail(@NonNull EmailDetails emailDetails) {
      final MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper mime;
      try {
         mime = new MimeMessageHelper(message, true, "UTF-8");
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
