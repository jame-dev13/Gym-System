package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PeriodicalEarning(
   @JsonProperty("totalEarned") BigDecimal totalEarned,
   @JsonProperty("membership") String membership,
   @JsonProperty("period") String period,
   @JsonProperty("rank") long rank
) {
}
