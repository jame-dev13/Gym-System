package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.PeriodicalEarningByYearResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalEarned;
import com.jame.dev.gymApp.features.metrics.api.response.TotalPerMonthResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.EarningMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.*;
import com.jame.dev.gymApp.features.metrics.domain.repository.EarningMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEarningMetricValues;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EarningMetricsApplicationService implements EarningMetricsService {
   private final EarningMetricsRepository repo;

   @Override
   @Cacheable(
      value = CacheEarningMetricValues.EARNING_TOTAL,
      unless = "#result == null"
   )
   public TotalEarned getTotal() {
      return repo.calculateTotalEarned();
   }

   @Override
   @Cacheable(
      value = CacheEarningMetricValues.EARNING_PER_MONTH,
      unless = "#result == null || #result.isEmpty()"
   )
   public List<TotalPerMonthResponse> getTotalPerMonth() {
      final List<TotalPerMonth> totalPerMonthDtoList = repo.calculateTotalPerMonth();
      return totalPerMonthDtoList.stream()
         .collect(Collectors.groupingBy(
            TotalPerMonth::year,
            Collectors.mapping(
               dto ->
                  new MonthTotal(dto.month(), dto.total()),
               Collectors.toUnmodifiableList()
            )
         ))
         .entrySet()
         .stream()
         .map(entry -> new TotalPerMonthResponse(entry.getKey(), entry.getValue()))
         .toList();
   }

   @Override
   @Cacheable(
      value = CacheEarningMetricValues.EARNING_MEMBERSHIP_TYPE,
      unless = "#result == null || #result.isEmpty()"
   )
   public List<TotalPerMembershipTypeDto> getTotalPerMembershipType() {
      final List<TotalPerMembershipTypeDto> dtoList = repo.calculateTotalPerMembership();
      return dtoList.isEmpty() ? List.of() : dtoList;
   }

   @Override
   @Cacheable(
      value = CacheEarningMetricValues.EARNING_PERIODICAL,
      unless = "#result == null || #result.isEmpty()"
   )
   public List<PeriodicalEarningByYearResponse> getPeriodicalEarnings() {
      return repo.calculatePeriodicalEarnings()
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
         .toList();
   }
}
