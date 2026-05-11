package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TotalPerMonth(
        @JsonProperty("year") Integer year,
        @JsonProperty("month") String month,
        @JsonProperty("total") BigDecimal total
) {
}