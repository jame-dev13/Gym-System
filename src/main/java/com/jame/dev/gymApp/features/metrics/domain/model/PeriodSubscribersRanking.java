package com.jame.dev.gymApp.features.metrics.domain.model;

public record PeriodSubscribersRanking(
   int year,
   String period,
   String subscriptionType,
   long subscriptionCount,
   long rank
) {
}
