package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PeriodDtoOutput(
        @JsonProperty("id") Long id,
        @JsonProperty("period") Period period,
        @JsonProperty("startDate") LocalDate startDate,
        @JsonProperty("endDate") LocalDate endDate
        ) {
}
