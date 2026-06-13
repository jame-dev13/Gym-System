package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SubscriptionCheckoutResponse(
    @JsonIgnore String sessionId,
    @JsonProperty("sessionUrl") String sessionUrl
) {
}
