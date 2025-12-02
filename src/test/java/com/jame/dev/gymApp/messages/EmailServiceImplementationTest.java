package com.jame.dev.gymApp.messages;

import com.jame.dev.gymApp.messages.service.EmailServiceImplementation;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.model.messages.EmailDetailsWAttachment;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

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
   void sendSimpleEmail() {
      doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
      boolean mailSent = service.sendSimpleEmail(emailDetails);

      assertTrue(mailSent, "Mail should had sent.");
      final ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      verify(javaMailSender, times(1)).send(captor.capture());
      SimpleMailMessage sent = captor.getValue();
      assertAll("Not null, and same properties.",
              () -> assertNotNull(sent, "Should not be null."),
              () -> assertEquals(emailDetails.recipient(), sent.getTo()[0], "Should be the same recipient"),
              () -> assertEquals(emailDetails.msgBody(), sent.getText(), "Should be the same msgBody."),
              () -> assertEquals(emailDetails.subject(), sent.getSubject(), "Should be the same subject"));
   }

   @Test
   void sendMailWithAttachment() throws IOException, MessagingException {
      final MimeMessage mimeMessage = new MimeMessage((Session) null);
      when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
      doNothing().when(javaMailSender).send(any(MimeMessage.class));

      final boolean mailSent = service.sendMailWithAttachment(emailDetailsWAttachment);

      assertTrue(mailSent, "Mail should had sent.");
      final ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
      verify(javaMailSender).send(captor.capture());

      final MimeMessage sent = captor.getValue();

      assertAll("Not null, same subject and mail's sent.",
              () -> assertNotNull(sent, "Should not be null."),
              () -> assertEquals(emailDetailsWAttachment.subject(), sent.getSubject(), "Should be the same subject."),
              () -> assertTrue(mailSent, "The mail should had sent.")
      );

   }
}