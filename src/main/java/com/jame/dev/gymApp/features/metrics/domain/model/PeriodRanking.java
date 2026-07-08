package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PeriodRanking(
   @JsonProperty("period") String period,
   @JsonProperty("subscriptionType") String subscriptionType,
   @JsonProperty("subscriptionCount") long subscriptionCount,
   @JsonProperty("rank") long rank
) {
}
