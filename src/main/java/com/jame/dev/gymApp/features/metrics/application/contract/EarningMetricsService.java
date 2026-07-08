package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.PeriodicalEarningByYearResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalEarned;
import com.jame.dev.gymApp.features.metrics.api.response.TotalPerMonthResponse;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;

import java.util.List;

public interface EarningMetricsService {
   TotalEarned getTotal();

   List<TotalPerMonthResponse> getTotalPerMonth();

   List<TotalPerMembershipTypeDto> getTotalPerMembershipType();

   List<PeriodicalEarningByYearResponse> getPeriodicalEarnings();
}
