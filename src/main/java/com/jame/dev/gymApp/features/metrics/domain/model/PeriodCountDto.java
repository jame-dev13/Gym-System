package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;

public record PeriodCountDto(
        @JsonProperty("period") Period period,
        @JsonProperty("count") long count
) {
}
