package com.jame.dev.gymApp.model.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubsPerMonthDto(
        @JsonProperty("month") String month,
        @JsonProperty("total") Long total
) {
}
