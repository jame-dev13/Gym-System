package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.*;
import com.jame.dev.gymApp.features.metrics.application.contract.EarningMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.model.PeriodicalEarning;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMonth;
import com.jame.dev.gymApp.features.metrics.domain.model.YearPeriodicalEarning;
import com.jame.dev.gymApp.features.metrics.domain.repository.EarningMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheMetricValues;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class EarningMetricsApplicationService implements EarningMetricsService {
   private final EarningMetricsRepository repo;

   @Override
   @Cacheable(
      value = CacheMetricValues.EARNINGS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public TotalEarned getTotal() {
      return repo.calculateTotalEarned();
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.EARNINGS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public EarningsByMonthResponse getTotalPerMonth() {
      return new EarningsByMonthResponse(
         repo.calculateTotalPerMonth().stream()
            .collect(Collectors.groupingBy(
               TotalPerMonth::year,
               Collectors.mapping(
                  dto -> new MonthTotal(dto.month(), dto.total()),
                  Collectors.toUnmodifiableList()
               )
            ))
            .entrySet()
            .stream()
            .map(entry -> new TotalPerMonthResponse(entry.getKey(), entry.getValue()))
            .toList()
      );
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.EARNINGS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public EarningsByMembershipTypeResponse getTotalPerMembershipType() {
      final var dtoList = repo.calculateTotalPerMembership();
      return new EarningsByMembershipTypeResponse(dtoList.isEmpty() ? List.of() : dtoList);
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.EARNINGS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PeriodicalEarningsResponse getPeriodicalEarnings() {
      return new PeriodicalEarningsResponse(
         repo.calculatePeriodicalEarnings()
            .stream()
            .collect(
               Collectors.groupingBy(
                  YearPeriodicalEarning::year,
                  Collectors.mapping(
                     data -> PeriodicalEarning.builder()
                        .totalEarned(data.totalEarned())
                        .membership(data.membership())
                        .period(data.period())
                        .rank(data.rank())
                        .build(),
                     Collectors.toUnmodifiableList()
                  )
               ))
            .entrySet()
            .stream()
            .map(data -> new PeriodicalEarningByYearResponse(data.getKey(), data.getValue()))
            .toList()
      );
   }
}
