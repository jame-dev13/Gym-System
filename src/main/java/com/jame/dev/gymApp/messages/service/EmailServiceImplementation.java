package com.jame.dev.gymApp.messages.service;

import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.model.messages.EmailDetailsWAttachment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
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
      try {
         SimpleMailMessage mailMessage = new SimpleMailMessage();
         mailMessage.setFrom(sender);
         mailMessage.setTo(emailDetails.recipient());
         mailMessage.setText(emailDetails.msgBody());
         mailMessage.setSubject(emailDetails.subject());

         javaMailSender.send(mailMessage);
         log.info("Mail message sent with success.");
         return CompletableFuture.completedFuture(true);
      } catch (MailException e) {
         log.error("Cannot sent mail message: ", e);
         return CompletableFuture.completedFuture(false);
      }
   }

   @Override
   public CompletableFuture<Boolean> sendMailWithAttachment(@NonNull EmailDetailsWAttachment emailDetails) {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper;
      try {
         mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
         mimeMessage.setFrom(this.sender);
         mimeMessageHelper.setTo(emailDetails.recipient());
         mimeMessageHelper.setText(emailDetails.msgBody(), true);
         mimeMessageHelper.setSubject(emailDetails.subject());

         final ClassPathResource classPathResource = new ClassPathResource("static/" + emailDetails.attachment());
         final Resource resource = Optional.of(classPathResource)
                 .filter(Resource::exists)
                 .orElseThrow(() -> new IOException("Resource not found."));
         mimeMessageHelper.addAttachment(resource.getFilename(), resource);
         javaMailSender.send(mimeMessage);
         log.info("Mail with attachment sent successfully.");
         return CompletableFuture.completedFuture(true);
      } catch (MessagingException | MailException | IOException e) {
         log.error("Cannot sent the mail with attachment: ", e);
         return CompletableFuture.completedFuture(false);
      }
   }

   @Override
   public String html(String to, String code) {
      return String.format(HTML, to, code);
   }
}
