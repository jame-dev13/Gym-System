package com.jame.dev.gymApp.features.subscription.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;

public record PeriodResponse(
   @JsonProperty("periodType") Period periodType,
   @JsonProperty("periodStr") String periodStr
) {
}
