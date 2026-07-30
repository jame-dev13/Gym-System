package com.jame.dev.gymApp.features.notification.application.dto;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

public record SubscriberNotificationFactoryDtoInput(
   SubscriptionEntity subscription,
   int rangeDays
) {
}
