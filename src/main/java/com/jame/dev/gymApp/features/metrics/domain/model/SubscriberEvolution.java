package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriberEvolution(
   @JsonProperty("month") String month,
   @JsonProperty("subscribersNum") long subscribersNum
) {
}
