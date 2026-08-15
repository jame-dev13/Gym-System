package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import org.springframework.security.core.Authentication;

public interface PaymentMetricsSubscriberService {

   AnnualResumeResponse getAnnualResume(final Authentication authentication);
   InvestmentMonthEvolutionResponse getInvestmentMonthEvolution(final Authentication authentication);

}
