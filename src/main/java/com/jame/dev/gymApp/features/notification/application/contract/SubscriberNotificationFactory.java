package com.jame.dev.gymApp.features.notification.application.contract;

import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.dto.SubscriberNotificationFactoryDtoInput;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

public interface SubscriberNotificationFactory {

   SubscriberNotificationResponse createFromEntity(SubscriberNotificationEntity entity);

   SubscriberNotificationEntity createFromInput(SubscriberNotificationFactoryDtoInput input);

   default SubscriberNotificationEntity from(SubscriptionEntity subscription, int rangeDays) {
      return SubscriberNotificationEntity.builder()
         .subscription(subscription)
         .rangeNotificationDays(rangeDays)
         .build();
   }
}
