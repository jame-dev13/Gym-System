package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.metrics.domain.model.PeriodicalEarning;

import java.util.List;

public record PeriodicalEarningByYearResponse(
   @JsonProperty("year") Integer year,
   @JsonProperty("periodicalEarnings") List<PeriodicalEarning> periodicalEarnings
) {
}
