package com.jame.dev.gymApp.metrics.service.in;

import com.jame.dev.gymApp.model.dto.out.MonthTotal;
import com.jame.dev.gymApp.model.metrics.TotalPerMembershipTypeDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface EarningMetricsService {
   BigDecimal getTotal();

   Map<Integer, List<MonthTotal>> getTotalPerMonth();

   List<TotalPerMembershipTypeDto> getTotalPerMembershipType();
}
