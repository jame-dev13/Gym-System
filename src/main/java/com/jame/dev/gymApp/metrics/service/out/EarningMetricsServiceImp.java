package com.jame.dev.gymApp.metrics.service.out;

import com.jame.dev.gymApp.metrics.repo.EarningMetricsRepository;
import com.jame.dev.gymApp.metrics.service.in.EarningMetricsService;
import com.jame.dev.gymApp.model.dto.out.MonthTotal;
import com.jame.dev.gymApp.model.metrics.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.model.metrics.TotalPerMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EarningMetricsServiceImp implements EarningMetricsService {
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
