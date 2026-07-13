package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalInvestment;

public interface PaymentMetricsSubscriberService {

   TotalInvestment getTotalExpend(final long customerId);
   AnnualResumeResponse getAnnualResume(final long customerId);
   InvestmentMonthEvolutionResponse getInvestmentMonthEvolution(final long customerId);

}
