package com.jame.dev.gymApp.features.subscription.domain.event;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import lombok.Builder;

@Builder
public record StripeSessionPaymentEvent(
   String sessionId,
   String intentId,
   String subscriptionId,
   SubscriptionEntity subscriptionEntity,
   boolean isPhysicSession
) {
}
