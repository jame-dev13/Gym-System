package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SubscriptionCheckoutResponse(
   @JsonProperty("sessionUrl") String sessionUrl,
   @JsonIgnore String sessionId,
   @JsonIgnore String paymentIndent,
   @JsonIgnore String paymentSubscription
) {
}
