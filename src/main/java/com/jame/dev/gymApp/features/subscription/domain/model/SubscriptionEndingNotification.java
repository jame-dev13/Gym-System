package com.jame.dev.gymApp.features.subscription.domain.model;

import java.time.LocalDate;

public record SubscriptionEndingNotification(
   String subscriberEmail,
   LocalDate startDate,
   LocalDate endingDate,
   Period period
) {
}
