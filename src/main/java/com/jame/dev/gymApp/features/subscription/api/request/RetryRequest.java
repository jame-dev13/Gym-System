package com.jame.dev.gymApp.features.subscription.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;

public record RetryRequest(
   @JsonProperty("sessionId")
   @NotEmptyNull  String sessionId
) {
}
