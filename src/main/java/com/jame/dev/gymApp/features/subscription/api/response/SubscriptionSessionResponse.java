package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionSessionResponse(
   @JsonProperty("checkout")
   SubscriptionCheckoutResponse checkout,
   @JsonProperty("subscription")
   SubscriptionResponse subscription
) {
}
