package com.jame.dev.gymApp.metrics.service.in;

import com.jame.dev.gymApp.model.metrics.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.model.metrics.TotalPerMonthDto;

import java.math.BigDecimal;
import java.util.List;

public interface EarningMetricsService {
   BigDecimal getTotal();

   List<TotalPerMonthDto> getTotalPerMonth();

   List<TotalPerMembershipTypeDto> getTotalPerMembershipType();
}
