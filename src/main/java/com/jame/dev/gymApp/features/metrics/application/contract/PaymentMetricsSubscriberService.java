package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;

public interface PaymentMetricsSubscriberService {

   AnnualResumeResponse getAnnualResume(final AuthPrincipal principal);
   InvestmentMonthEvolutionResponse getInvestmentMonthEvolution(final AuthPrincipal principal);

}
