package com.jame.dev.gymApp.metrics.service.in;

import com.jame.dev.gymApp.model.metrics.PeriodCountDto;
import com.jame.dev.gymApp.model.metrics.SubsPerMembership;
import com.jame.dev.gymApp.model.metrics.SubsPerMonthDto;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionMetricsService {
   long getTotalSubscriptions();

   long getSubscriptionsBefore(@NonNull final LocalDate date);

   List<PeriodCountDto> getSubscriptionsPerPeriod();

   List<SubsPerMonthDto> getSubscriptionsPerMonth();

   List<SubsPerMembership> getSubscriptionsPerMembership();
}
