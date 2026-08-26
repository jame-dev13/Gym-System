package com.jame.dev.gymApp.features.subscription.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;

public record CompletedCheckoutResult(
   @JsonProperty("paymentStatus") PaymentStatus paymentStatus,
   @JsonProperty("subscription") SubscriptionResponse subscription
   ) {
}
