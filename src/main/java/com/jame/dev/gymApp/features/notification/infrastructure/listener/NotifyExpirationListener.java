package com.jame.dev.gymApp.features.notification.infrastructure.listener;

import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.application.dto.NotifiableInfo;
import com.jame.dev.gymApp.features.notification.domain.event.NotifyExpirationEvent;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotifyExpirationListener {

   private final EmailService emailService;

   @EventListener(NotifyExpirationEvent.class)
   public void sendNotificationMail(final NotifyExpirationEvent event) {
      final var notifiableSet = event.emailAdressSet();
      for (NotifiableInfo ni : notifiableSet) {
         emailService.sendSimpleEmail(
            EmailDetails.builder()
               .recipient(ni.email())
               .msgBody(HtmlTemplates.notifyExpirationTemplate(ni.email(), String.valueOf(ni.daysLeft())))
               .subject("Gym Subscription expiration.")
               .build()
         );
      }
   }
}
