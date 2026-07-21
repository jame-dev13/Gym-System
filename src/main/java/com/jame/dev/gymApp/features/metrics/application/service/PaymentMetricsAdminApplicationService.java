package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsAdminService;
import com.jame.dev.gymApp.features.metrics.domain.repository.PaymentMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheMetricValues;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMetricsAdminApplicationService implements PaymentMetricsAdminService {
   private final PaymentMetricsRepository paymentMetricsRepository;

   @Override
   @Cacheable(
      value = CacheMetricValues.PAYMENTS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public AnnualResumeResponse getAnnualResume() {
      return paymentMetricsRepository.calculateAnnualResume();
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.PAYMENTS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public InvestmentMonthEvolutionResponse getInvestmentMonthEvolution() {
      return new InvestmentMonthEvolutionResponse(paymentMetricsRepository.calculatePaymentEvolutionAlongMonths());
   }
}
