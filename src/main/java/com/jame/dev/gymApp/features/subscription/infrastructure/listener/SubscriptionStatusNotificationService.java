package com.jame.dev.gymApp.features.subscription.infrastructure.listener;


import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.event.EmailDetailsEvent;
import com.jame.dev.gymApp.features.subscription.domain.event.SubscriptionNotificationEvent;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.template.HTMLSubscriptionPeriodTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class SubscriptionStatusNotificationService {
   private final ApplicationEventPublisher eventPublisher;

   @EventListener(SubscriptionNotificationEvent.class)
   @Async("taskExecutor")
   public void onSubscriptionNotificationListReceived(final SubscriptionNotificationEvent event) {
      event.notifiableList()
         .forEach(subEnding -> {
            final long daysUntilEnds = Math.abs(
               ChronoUnit
                  .DAYS
                  .between(LocalDate.now(), subEnding.endingDate())
            );
            final var emailDetails = EmailDetails.builder()
               .subject("Subscription Period Status")
               .msgBody(
                  HTMLSubscriptionPeriodTemplate
                     .buildTemplateFrom(subEnding, daysUntilEnds))
               .recipient(subEnding.subscriberEmail())
               .build();
            eventPublisher.publishEvent(new EmailDetailsEvent(emailDetails));
         });
   }
}
