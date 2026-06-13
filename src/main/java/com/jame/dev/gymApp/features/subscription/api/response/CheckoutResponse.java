package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CheckoutResponse(
    @JsonProperty("sessionId") String sessionId,
    @JsonProperty("sessionUrl") String sessionUrl
) {
}
