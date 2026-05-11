package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionMetricsService {
   long getTotalSubscriptions();

   long getSubscriptionsBefore(@NonNull final LocalDate date);

   List<SubsPerMonthDto> getSubscriptionsPerMonth();

   List<SubsPerMembership> getSubscriptionsPerMembership();
}
