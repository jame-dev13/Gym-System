package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.*;
import com.jame.dev.gymApp.features.metrics.application.contract.SubscriptionMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.PeriodRanking;
import com.jame.dev.gymApp.features.metrics.domain.model.PeriodSubscribersRanking;
import com.jame.dev.gymApp.features.metrics.domain.repository.SubscriptionMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheMetricValues;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionMetricsApplicationService implements SubscriptionMetricsService {
   private final SubscriptionMetricsRepository repo;

   @Override
   @Cacheable(
      value = CacheMetricValues.SUBSCRIPTIONS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public TotalSubscriptions getTotalSubscriptions() {
      return repo.countAllSubscriptionDistinct();
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.SUBSCRIPTIONS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public MembershipRankingsResponse getMembershipRanking() {
      return new MembershipRankingsResponse(repo.calculateMembershipRanking());
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.SUBSCRIPTIONS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PeriodRankingsResponse getPeriodRanking() {
      return new PeriodRankingsResponse(
         repo.calculatePeriodWithMostSubscribers()
            .stream()
            .collect(
               Collectors.groupingBy(
                  PeriodSubscribersRanking::year,
                  Collectors.mapping(dto ->
                        PeriodRanking.builder()
                           .period(dto.period())
                           .subscriptionType(dto.subscriptionType())
                           .subscriptionCount(dto.subscriptionCount())
                           .rank(dto.rank())
                           .build(),
                     Collectors.toUnmodifiableList())))
            .entrySet()
            .stream()
            .map((map) -> new PeriodRankingPerYear(map.getKey(), map.getValue()))
            .toList()
      );
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.SUBSCRIPTIONS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public TotalSubscriptions getSubscriptionsBefore(@NonNull LocalDate date) {
      return repo.countByStartDateBefore(date);
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.SUBSCRIPTIONS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public SubscriptionsPerMonthResponse getSubscriptionsPerMonth() {
      return new SubscriptionsPerMonthResponse(repo.countSubsByMonth());
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.SUBSCRIPTIONS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public SubscriptionsPerMembershipResponse getSubscriptionsPerMembership() {
      return new SubscriptionsPerMembershipResponse(repo.countSubsByMembership());
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.SUBSCRIPTIONS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public SubscriptionAnnualResumeResponse getAnnualResume() {
      return repo.calculateAnnualResume();
   }
}
