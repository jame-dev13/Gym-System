package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.EarningsByMembershipTypeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.EarningsByMonthResponse;
import com.jame.dev.gymApp.features.metrics.api.response.PeriodicalEarningsResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalEarned;

public interface EarningMetricsService {
   TotalEarned getTotal();

   EarningsByMonthResponse getTotalPerMonth();

   EarningsByMembershipTypeResponse getTotalPerMembershipType();

   PeriodicalEarningsResponse getPeriodicalEarnings();
}
