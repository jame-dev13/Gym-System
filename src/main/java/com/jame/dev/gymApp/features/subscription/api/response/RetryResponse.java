package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RetryResponse(
   @JsonProperty("sessionUrl") String sessionUrl
) {
}
