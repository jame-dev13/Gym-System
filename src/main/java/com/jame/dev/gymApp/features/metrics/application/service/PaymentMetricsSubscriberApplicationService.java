package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalInvestment;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsSubscriberService;
import com.jame.dev.gymApp.features.metrics.domain.repository.PaymentMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheMetricValues;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class PaymentMetricsSubscriberApplicationService implements PaymentMetricsSubscriberService {
   private final PaymentMetricsRepository paymentMetricsRepository;

   @Override
   @Cacheable(
      value = CacheMetricValues.PAYMENTS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public TotalInvestment getTotalExpend(long customerId) {
      return paymentMetricsRepository.calculateTotalAmountExpended(customerId);
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.PAYMENTS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public AnnualResumeResponse getAnnualResume(long customerId) {
      return paymentMetricsRepository.calculateAnnualResume(customerId);
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.PAYMENTS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public InvestmentMonthEvolutionResponse getInvestmentMonthEvolution(long customerId) {
      return new InvestmentMonthEvolutionResponse(paymentMetricsRepository.calculatePaymentEvolutionAlongMonths(customerId));
   }
}
