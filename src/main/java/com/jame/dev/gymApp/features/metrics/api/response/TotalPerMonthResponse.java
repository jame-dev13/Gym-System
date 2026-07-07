package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;

import java.util.List;

public record TotalPerMonthResponse(
   @JsonProperty("year") Integer year,
   @JsonProperty("monthsTotal") List<MonthTotal> monthTotals
) {
}
