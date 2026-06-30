package com.jame.dev.gymApp.features.subscription.domain.event;

import lombok.Builder;

@Builder
public record CompletedCheckoutEvent(
    String stripeSessionId,
    String stripePaymentIntentId,
    String stripeSubscriptionId,
    String customerEmail
) {}
