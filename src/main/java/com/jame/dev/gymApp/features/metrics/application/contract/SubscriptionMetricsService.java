package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.MembershipRankingsResponse;
import com.jame.dev.gymApp.features.metrics.api.response.PeriodRankingsResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriptionsPerMembershipResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriptionsPerMonthResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalSubscriptions;
import lombok.NonNull;

import java.time.LocalDate;

public interface SubscriptionMetricsService {
   TotalSubscriptions getTotalSubscriptions();

   MembershipRankingsResponse getMembershipRanking();

   PeriodRankingsResponse getPeriodRanking();

   TotalSubscriptions getSubscriptionsBefore(@NonNull final LocalDate date);

   SubscriptionsPerMonthResponse getSubscriptionsPerMonth();

   SubscriptionsPerMembershipResponse getSubscriptionsPerMembership();
}
