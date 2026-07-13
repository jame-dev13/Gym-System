package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PeriodicalEarningsResponse(
   @JsonProperty("content") List<PeriodicalEarningByYearResponse> content
) {
}
