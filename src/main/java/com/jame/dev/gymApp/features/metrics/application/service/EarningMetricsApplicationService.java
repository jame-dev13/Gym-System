package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.domain.repository.EarningMetricsRepository;
import com.jame.dev.gymApp.features.metrics.application.contract.EarningMetricsService;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EarningMetricsApplicationService implements EarningMetricsService {
   private final EarningMetricsRepository repo;

   @Override
   public BigDecimal getTotal() {
      return repo.calculateTotalEarned();
   }

   @Override
   public Map<Integer, List<MonthTotal>> getTotalPerMonth() {
      final List<TotalPerMonth> totalPerMonthDtoList = repo.calculateTotalPerMonth();
      return totalPerMonthDtoList.stream()
              .collect(Collectors.groupingBy(
                      TotalPerMonth::year,
                      Collectors.mapping(
                              dto ->
                                      new MonthTotal(dto.month(), dto.total()),
                              Collectors.toUnmodifiableList()
                      )
              ));
   }

   @Override
   public List<TotalPerMembershipTypeDto> getTotalPerMembershipType() {
      final List<TotalPerMembershipTypeDto> dtoList = repo.calculateTotalPerMembership();
      return dtoList.isEmpty() ? List.of() : dtoList;
   }

}
