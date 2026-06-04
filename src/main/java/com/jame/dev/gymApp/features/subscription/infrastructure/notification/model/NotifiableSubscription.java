package com.jame.dev.gymApp.features.subscription.infrastructure.notification.model;

import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;

public record NotifiableSubscription(
   String customerEmail,
   PeriodEntity period
) {
}
