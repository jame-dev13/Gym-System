package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentMethod;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
   @JsonProperty("id") Long id,
   @JsonProperty("amount") BigDecimal amount,
   @JsonProperty("status") PaymentStatus status,
   @JsonProperty("paymentMethod") PaymentMethod paymentMethod,
   @JsonProperty("createdAt") String createdAt
) {
}
