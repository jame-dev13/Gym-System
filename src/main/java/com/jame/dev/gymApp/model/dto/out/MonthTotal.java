package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record MonthTotal(
        @JsonProperty("month") String month,
        @JsonProperty("total") BigDecimal total
) {
}
