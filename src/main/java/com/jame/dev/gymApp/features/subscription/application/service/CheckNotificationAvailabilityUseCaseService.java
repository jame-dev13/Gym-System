package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.api.response.NotificationAvailabilityResponse;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionNotificationConfigData;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.model.NotificationStatus;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.usecases.CheckNotificationAvailabilityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CheckNotificationAvailabilityUseCaseService implements CheckNotificationAvailabilityUseCase {
   private final SubscriptionNotificationConfigData configData;
   private final StringRedisTemplate notificationTemplate;

   @Override
   public NotificationAvailabilityResponse checkAvailability() {
      final long ttl = notificationTemplate.getExpire(configData.lockKey(), TimeUnit.MILLISECONDS);
      return ttl > 0 ?
         new NotificationAvailabilityResponse(NotificationStatus.UNDER_LOCK, ttl) :
         new NotificationAvailabilityResponse(NotificationStatus.AVAILABLE, -1L);
   }
}
