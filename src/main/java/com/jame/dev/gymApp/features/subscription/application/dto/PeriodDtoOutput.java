package com.jame.dev.gymApp.features.subscription.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;

import java.time.LocalDate;

public record PeriodDtoOutput(
        @JsonProperty("id") Long id,
        @JsonProperty("period") Period period,
        @JsonProperty("startPeriod") LocalDate startPeriod,
        @JsonProperty("endPeriod") LocalDate endPeriod
) {
}
