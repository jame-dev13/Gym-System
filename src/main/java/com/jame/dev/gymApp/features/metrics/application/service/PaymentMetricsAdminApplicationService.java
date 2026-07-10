package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsAdminService;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.repository.PaymentMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CachePaymentMetricsValues;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMetricsAdminApplicationService implements PaymentMetricsAdminService {
   private final PaymentMetricsRepository paymentMetricsRepository;

   @Override
   @Cacheable(
      value = CachePaymentMetricsValues.RESUME,
      unless = "#result == null"
   )
   public AnnualResumeResponse getAnnualResume() {
      return paymentMetricsRepository.calculateAnnualResume();
   }

   @Override
   @Cacheable(
      value = CachePaymentMetricsValues.EVOLUTION,
      unless = "#result == null || #result.isEmpty()"
   )
   public List<MonthTotal> getInvestmentMonthEvolution() {
      return paymentMetricsRepository.calculatePaymentEvolutionAlongMonths();
   }
}
