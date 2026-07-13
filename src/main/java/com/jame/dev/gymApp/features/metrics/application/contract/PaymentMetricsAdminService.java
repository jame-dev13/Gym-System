package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;

public interface PaymentMetricsAdminService {

   AnnualResumeResponse getAnnualResume();

   InvestmentMonthEvolutionResponse getInvestmentMonthEvolution();
}
