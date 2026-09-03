package com.jame.dev.gymApp.features.subscription.domain.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.subscriptions.notification")
public record SubscriptionNotificationConfigData(
   String lockKey,
   int lockDays
) {
}
