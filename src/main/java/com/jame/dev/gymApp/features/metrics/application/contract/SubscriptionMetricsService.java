package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.MembershipRanking;
import com.jame.dev.gymApp.features.metrics.api.response.PeriodRankingPerYear;
import com.jame.dev.gymApp.features.metrics.api.response.TotalSubscriptions;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionMetricsService {
   TotalSubscriptions getTotalSubscriptions();

   List<MembershipRanking> getMembershipRanking();

   List<PeriodRankingPerYear> getPeriodRanking();

   TotalSubscriptions getSubscriptionsBefore(@NonNull final LocalDate date);

   List<SubsPerMonthDto> getSubscriptionsPerMonth();

   List<SubsPerMembership> getSubscriptionsPerMembership();
}
