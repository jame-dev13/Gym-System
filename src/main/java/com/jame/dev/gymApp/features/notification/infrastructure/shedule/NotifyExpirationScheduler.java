package com.jame.dev.gymApp.features.notification.infrastructure.shedule;

import com.jame.dev.gymApp.features.notification.application.dto.NotifiableInfo;
import com.jame.dev.gymApp.features.notification.domain.event.NotifyExpirationEvent;
import com.jame.dev.gymApp.features.notification.infrastructure.query.SubscriberNotifiableRetrieverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyExpirationScheduler {

   private final Clock clock;
   private final SubscriberNotifiableRetrieverRepository notifiableRepository;
   private final ApplicationEventPublisher applicationEventPublisher;

   @Scheduled(cron = "0 15 0 * * *", zone = "America/Mexico_City")
   public void publishExpirationNotifications() {
      final LocalDate today = LocalDate.now(clock);

      final LocalDateTime start = today.atStartOfDay();
      final LocalDateTime end = today.plusDays(1).atStartOfDay();

      final Set<NotifiableInfo> notifications =
         notifiableRepository.findAllNotificationAvailableMailAddressesByStartAndEnd(start, end);

      if (notifications.isEmpty()) return;
      applicationEventPublisher.publishEvent(new NotifyExpirationEvent(notifications));
   }
}
