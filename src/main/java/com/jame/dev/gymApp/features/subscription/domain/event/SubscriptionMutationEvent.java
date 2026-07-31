package com.jame.dev.gymApp.features.subscription.domain.event;

import java.time.LocalDate;

public record SubscriptionMutationEvent(
   long subscriptionId,
   LocalDate endingPeriodDate
) {
}
