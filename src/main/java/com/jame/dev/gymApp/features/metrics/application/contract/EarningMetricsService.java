package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface EarningMetricsService {
   BigDecimal getTotal();

   Map<Integer, List<MonthTotal>> getTotalPerMonth();

   List<TotalPerMembershipTypeDto> getTotalPerMembershipType();
}
