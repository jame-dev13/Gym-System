package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.metrics.domain.model.PeriodRanking;

import java.util.List;

public record PeriodRankingPerYear(
   @JsonProperty("year") int year,
   @JsonProperty("totalPerYear") List<PeriodRanking> totalPerYear
   ) {
}
