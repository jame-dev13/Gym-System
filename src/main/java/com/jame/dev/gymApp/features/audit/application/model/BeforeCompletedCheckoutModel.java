package com.jame.dev.gymApp.features.audit.application.model;

import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BeforeCompletedCheckoutModel(
   PaymentStatus paymentStatus,
   String customerEmail,
   BigDecimal amount
) {
}
