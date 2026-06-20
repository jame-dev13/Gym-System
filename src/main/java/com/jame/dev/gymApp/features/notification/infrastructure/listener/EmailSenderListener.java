package com.jame.dev.gymApp.features.notification.infrastructure.listener;

import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.domain.event.EmailDetailsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSenderListener {
   private final EmailService emailService;

   @EventListener(EmailDetailsEvent.class)
   public void sendEmailDetails(EmailDetailsEvent event) {
      emailService.sendSimpleEmail(event.emailDetails());
   }
}
