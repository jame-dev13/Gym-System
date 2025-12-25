package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Period;

import java.time.LocalDate;

public record PeriodDtoOutput(
        @JsonProperty("id") Long id,
        @JsonProperty("period") Period period,
        @JsonProperty("startPeriod") LocalDate startPeriod,
        @JsonProperty("endPeriod") LocalDate endPeriod
) {
}
