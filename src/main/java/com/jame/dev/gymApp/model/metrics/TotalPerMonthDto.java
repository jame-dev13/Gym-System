package com.jame.dev.gymApp.model.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TotalPerMonthDto(
        @JsonProperty("year") Integer year,
        @JsonProperty("month") String month,
        @JsonProperty("total") BigDecimal total
) {
}