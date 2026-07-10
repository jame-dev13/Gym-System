package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;

import java.util.List;

public interface PaymentMetricsAdminService {

   AnnualResumeResponse getAnnualResume();

   List<MonthTotal> getInvestmentMonthEvolution();
}
