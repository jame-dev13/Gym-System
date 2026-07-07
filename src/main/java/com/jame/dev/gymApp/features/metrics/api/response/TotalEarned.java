package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TotalEarned(
   @JsonProperty("total") BigDecimal total
) {
}
