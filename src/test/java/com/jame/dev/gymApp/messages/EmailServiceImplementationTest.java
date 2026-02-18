package com.jame.dev.gymApp.messages;

import com.jame.dev.gymApp.messages.service.EmailServiceImplementation;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.model.messages.EmailDetailsWAttachment;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplementationTest {

   @Mock
   private JavaMailSender javaMailSender;

   @InjectMocks
   private EmailServiceImplementation service;

   private final EmailDetails emailDetails = EmailDetails.builder()
           .recipient("someone@mail.com")
           .msgBody("Hello form messaging service")
           .subject("Welcome")
           .build();
   private final EmailDetailsWAttachment emailDetailsWAttachment = EmailDetailsWAttachment.builder()
           .recipient("someone@mail.com")
           .msgBody("<p>Welcome!</p>")
           .subject("Welcome")
           .attachment("logo_me.png")
           .build();

   @Test
   void sendSimpleEmail() throws ExecutionException, InterruptedException {
      final MimeMessage mimeMessage = new MimeMessage((Session) null);
      when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
      doNothing().when(javaMailSender).send(any(MimeMessage.class));

      final CompletableFuture<Boolean> mailSent = service.sendSimpleEmail(emailDetails);

      assertTrue(mailSent.get(), "Mail should had sent.");

      final ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
      verify(javaMailSender, times(1)).send(captor.capture());
      final MimeMessage sent = captor.getValue();
      assertAll("Not null, and same properties.",
              () -> assertNotNull(sent, "Should not be null."),
              () -> assertEquals(emailDetails.subject(), sent.getSubject(), "Should be the same subject"));
   }
}