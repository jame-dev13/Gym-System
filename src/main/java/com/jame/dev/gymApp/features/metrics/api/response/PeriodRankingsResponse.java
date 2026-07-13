package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PeriodRankingsResponse(
   @JsonProperty("content") List<PeriodRankingPerYear> content
) {
}
