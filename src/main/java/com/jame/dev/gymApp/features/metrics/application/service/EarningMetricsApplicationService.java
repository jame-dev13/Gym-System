package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.TotalEarned;
import com.jame.dev.gymApp.features.metrics.api.response.TotalPerMonthResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.EarningMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMonth;
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
      value = CacheEarningMetricValues.EARNING_TOTAL
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

}
