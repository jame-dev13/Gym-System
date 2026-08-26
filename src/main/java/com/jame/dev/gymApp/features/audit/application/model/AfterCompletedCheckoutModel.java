package com.jame.dev.gymApp.features.audit.application.model;

import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AfterCompletedCheckoutModel(
   PaymentStatus paymentStatus,
   String customerEmail,
   BigDecimal amount,
   Membership membership,
   SubscriptionStatus subscriptionStatus
) {
}
