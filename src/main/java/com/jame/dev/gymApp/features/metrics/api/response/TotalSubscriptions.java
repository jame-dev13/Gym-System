package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TotalSubscriptions(
   @JsonProperty("total") long total
) {
}
