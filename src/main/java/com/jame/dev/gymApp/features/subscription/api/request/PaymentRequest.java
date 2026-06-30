package com.jame.dev.gymApp.features.subscription.api.request;

import lombok.Builder;

@Builder
public record PaymentRequest(
   String sessionId,
   String intentId,
   String subscriptionSessionId,
   boolean isPhysical,
   long subscriptionId
) {
}
