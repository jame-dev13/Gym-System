package com.jame.dev.gymApp.features.subscription.domain.event;

public record CompletedCheckoutEvent(
    String stripeSessionId,
    String stripePaymentIntentId,
    String stripeSubscriptionId,
    String customerEmail
) {}
