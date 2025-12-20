package com.jame.dev.gymApp.model.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Period;

public record PeriodCountDto(
        @JsonProperty("period") Period period,
        @JsonProperty("count") long count
) {
}
