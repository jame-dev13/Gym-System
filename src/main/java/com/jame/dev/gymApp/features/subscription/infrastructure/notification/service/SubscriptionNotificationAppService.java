package com.jame.dev.gymApp.features.subscription.infrastructure.notification.service;

import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.SubscriptionRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.model.NotifiableSubscription;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.template.HTMLSubscriptionPeriodTemplate;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class SubscriptionNotificationAppService {

   private final SubscriptionRepository subscriptionRepository;
   private final EmailService emailService;
   private final StringRedisTemplate notificationTemplate;
   private static final String UNIQUE_KEY = "notify:subscription:periods";

   public void notifySubscriptionEnds() {
      if (notificationTemplate.hasKey(UNIQUE_KEY)) {
         throw new NotificationException("Cannot perform massive notification now, it has been already done.");
      }

      notificationTemplate.opsForValue().set(
         UNIQUE_KEY,
         Instant.now().atZone(ZoneId.systemDefault()).toString(),
         Duration.ofDays(1)
      );

      final List<NotifiableSubscription> subscriptions = subscriptionRepository.findAll()
         .stream()
         .map(s -> {
               PeriodEntity period = s.getSubscriptionPeriods().getLast();
               return new NotifiableSubscription(s.getCustomer().getUser().getEmail(), period);
            }
         )
         .toList();

      subscriptions
         .forEach(ns -> {
            final long daysUntilEnds = Math.abs(ChronoUnit.DAYS
               .between(LocalDate.now(), ns.period().getEndPeriod()));
            emailService.sendSimpleEmail(
               EmailDetails.builder()
                  .subject("Subscription Period Status")
                  .msgBody(HTMLSubscriptionPeriodTemplate.buildTemplateFrom(ns, daysUntilEnds))
                  .recipient(ns.customerEmail())
                  .build());
         });
   }
}
