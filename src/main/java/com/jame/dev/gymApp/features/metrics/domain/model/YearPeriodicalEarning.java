package com.jame.dev.gymApp.features.metrics.domain.model;

import java.math.BigDecimal;

public record YearPeriodicalEarning(
   int year,
   String period,
   String membership,
   BigDecimal totalEarned,
   long rank
) {
}
