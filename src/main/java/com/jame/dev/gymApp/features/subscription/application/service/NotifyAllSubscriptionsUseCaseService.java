package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.subscription.domain.event.SubscriptionNotificationEvent;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionNotificationConfigData;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.usecases.NotifyAllSubscriptionsUseCase;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class NotifyAllSubscriptionsUseCaseService implements NotifyAllSubscriptionsUseCase {
   private final SubscriptionNotificationConfigData configData;
   private final StringRedisTemplate notificationTemplate;
   private final SubscriptionQueryRepository subscriptionRepository;
   private final ApplicationEventPublisher eventPublisher;

   @Override
   public void notifySubscriptions() {
      if (notificationTemplate.hasKey(configData.lockKey())) {
         throw new NotificationException("Cannot perform massive notification now, it has been already done.");
      }

      notificationTemplate.opsForValue().set(
         configData.lockKey(),
         Instant.now().atZone(ZoneId.systemDefault()).toString(),
         Duration.ofDays(configData.lockDays())
      );

      final var subscriptionEndingList = subscriptionRepository.findAllSubscriptionEndings();
      if (subscriptionEndingList.isEmpty())
         throw new NotificationException("There are no subscribers to notify.");
      eventPublisher.publishEvent(new SubscriptionNotificationEvent(subscriptionEndingList));
   }
}
