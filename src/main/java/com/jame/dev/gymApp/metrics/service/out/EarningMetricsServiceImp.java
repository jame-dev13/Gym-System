package com.jame.dev.gymApp.metrics.service.out;

import com.jame.dev.gymApp.metrics.repo.EarningMetricsRepository;
import com.jame.dev.gymApp.metrics.service.in.EarningMetricsService;
import com.jame.dev.gymApp.model.metrics.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.model.metrics.TotalPerMonthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EarningMetricsServiceImp implements EarningMetricsService {
   private final EarningMetricsRepository repo;

   @Override
   public BigDecimal getTotal() {
      return repo.calculateTotalEarned();
   }

   @Override
   public List<TotalPerMonthDto> getTotalPerMonth() {
      final List<TotalPerMonthDto> totalPerMonthDtoList = repo.calculateTotalPerMonth();
      if(totalPerMonthDtoList.isEmpty()) return List.of();
      return totalPerMonthDtoList;
   }

   @Override
   public List<TotalPerMembershipTypeDto> getTotalPerMembershipType() {
      final List<TotalPerMembershipTypeDto> dtoList = repo.calculateTotalPerMembership();
      if(dtoList.isEmpty()) return List.of();
      return dtoList;
   }

}
