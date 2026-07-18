package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.*;
import lombok.NonNull;

import java.time.LocalDate;

public interface SubscriptionMetricsService {
   TotalSubscriptions getTotalSubscriptions();

   MembershipRankingsResponse getMembershipRanking();

   PeriodRankingsResponse getPeriodRanking();

   TotalSubscriptions getSubscriptionsBefore(@NonNull final LocalDate date);

   SubscriptionsPerMonthResponse getSubscriptionsPerMonth();

   SubscriptionsPerMembershipResponse getSubscriptionsPerMembership();

   SubscriptionAnnualResumeResponse getAnnualResume();
}
