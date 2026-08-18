package com.jame.dev.gymApp.features.metrics.application.service;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.InvestmentMonthEvolutionResponse;
import com.jame.dev.gymApp.features.metrics.application.contract.PaymentMetricsSubscriberService;
import com.jame.dev.gymApp.features.metrics.domain.repository.PaymentMetricsRepository;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheMetricValues;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class PaymentMetricsSubscriberApplicationService implements PaymentMetricsSubscriberService {
   private final PaymentMetricsRepository paymentMetricsRepository;
   private final IdentityExtractorService identityExtractorService;

   @Override
   @Cacheable(
      value = CacheMetricValues.PAYMENTS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null"
   )
   public AnnualResumeResponse getAnnualResume(Authentication authentication) {
      final String subject = identityExtractorService.extract(authentication);
      return paymentMetricsRepository.calculateAnnualResume(subject);
   }

   @Override
   @Cacheable(
      value = CacheMetricValues.PAYMENTS,
      keyGenerator = "appKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public InvestmentMonthEvolutionResponse getInvestmentMonthEvolution(Authentication authentication) {
      final String subject = identityExtractorService.extract(authentication);
      return new InvestmentMonthEvolutionResponse(paymentMetricsRepository.calculatePaymentEvolutionAlongMonths(subject));
   }
}
