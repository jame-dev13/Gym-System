package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.TotalSubscriptions;
import com.jame.dev.gymApp.features.metrics.application.contract.SubscriptionMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
import com.jame.dev.gymApp.features.metrics.domain.repository.SubscriptionMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheSubsMetricsValues;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionMetricsApplicationService implements SubscriptionMetricsService {
   private final SubscriptionMetricsRepository repo;

   @Override
   @Cacheable(
      value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL, unless = "#result == null"
   )
   public TotalSubscriptions getTotalSubscriptions() {
      return repo.countAllSubscriptionDistinct();
   }

   @Override
   @Cacheable(
      value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL_BEFORE, unless = "#result == null"
   )
   public TotalSubscriptions getSubscriptionsBefore(@NonNull LocalDate date) {
      return repo.countByStartDateBefore(date);
   }

   @Override
   @Cacheable(
      value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL_PER_MONTH, unless = "#result == null || #result.isEmpty()"
   )
   public List<SubsPerMonthDto> getSubscriptionsPerMonth() {
      return repo.countSubsByMonth();
   }

   @Override
   @Cacheable(
      value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL_PER_MEMBERSHIP, unless = "#result == null || #result.isEmpty()"
   )
   public List<SubsPerMembership> getSubscriptionsPerMembership() {
      return repo.countSubsByMembership();
   }
}
