package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubsPerMonthDto(
        @JsonProperty("month") String month,
        @JsonProperty("total") Long total
) {
}
