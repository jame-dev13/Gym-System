package com.jame.dev.gymApp.features.subscription.domain.event;

import java.time.LocalDate;

public record SubscriptionFinalizedEvent(
   long subscriptionId,
   LocalDate endingPeriodDate
) {
}
