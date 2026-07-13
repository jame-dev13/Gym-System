package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalInvestment;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsSubscriberService;
import com.jame.dev.gymApp.features.metrics.domain.repository.PaymentMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CachePaymentMetricsValues;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentMetricsSubscriberApplicationService implements PaymentMetricsSubscriberService {
   private final PaymentMetricsRepository paymentMetricsRepository;

   @Override
   @Cacheable(
      value = CachePaymentMetricsValues.INVESTMENT,
      unless = "#result == null",
      key = "#customerId"
   )
   public TotalInvestment getTotalExpend(long customerId) {
      return paymentMetricsRepository.calculateTotalAmountExpended(customerId);
   }

   @Override
   @Cacheable(
      value = CachePaymentMetricsValues.RESUME,
      unless = "#result == null",
      key = "#customerId"
   )
   public AnnualResumeResponse getAnnualResume(long customerId) {
      return paymentMetricsRepository.calculateAnnualResume(customerId);
   }

   @Override
   @Cacheable(
      value = CachePaymentMetricsValues.EVOLUTION,
      unless = "#result == null || #result.content.isEmpty()",
      key = "#customerId"
   )
   public InvestmentMonthEvolutionResponse getInvestmentMonthEvolution(long customerId) {
      return new InvestmentMonthEvolutionResponse(paymentMetricsRepository.calculatePaymentEvolutionAlongMonths(customerId));
   }
}
