package com.jame.dev.gymApp.features.metrics.application.contract;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalInvestment;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;

import java.util.List;

public interface PaymentMetricsSubscriberService {

   TotalInvestment getTotalExpend(final long customerId);
   AnnualResumeResponse getAnnualResume(final long customerId);
   List<MonthTotal> getInvestmentMonthEvolution(final long customerId);

}
