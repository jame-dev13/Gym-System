package com.jame.dev.gymApp.features.subscription.domain.event;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEndingNotification;

import java.util.List;

public record SubscriptionNotificationEvent(
   List<SubscriptionEndingNotification> notifiableList
) {
}
