package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.metrics.infrastructure.annotations.EvictPaymentMetrics;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues;
import com.jame.dev.gymApp.features.subscription.api.request.PaymentRequest;
import com.jame.dev.gymApp.features.subscription.application.support.factory.PaymentFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreatePaymentUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.StripeSessionPaymentEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePaymentUseCaseService implements CreatePaymentUseCase {
   private final PaymentMutationRepository paymentMutationRepository;
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final PaymentFactory paymentFactory;

   @Override
   @Transactional
   @Caching(
      evict = {
         @CacheEvict(value = CacheValues.PAYMENTS, allEntries = true, cacheManager = "redisCacheManger"),
         @CacheEvict(value = CacheEvolutionMetricsValues.BILLINGS, allEntries = true, cacheManager = "redisCacheManager")
      }
   )
   @EvictPaymentMetrics
   public void create(PaymentRequest paymentRequest) {
      final SubscriptionEntity subscriptionEntity = subscriptionQueryRepository.findById(paymentRequest.subscriptionId())
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for id: " + paymentRequest.subscriptionId()));

      final StripeSessionPaymentEvent paymentEvent = StripeSessionPaymentEvent.builder()
         .sessionId(paymentRequest.sessionId())
         .intentId(paymentRequest.intentId())
         .subscriptionId(paymentRequest.subscriptionSessionId())
         .subscriptionEntity(subscriptionEntity)
         .paymentStatus(PaymentStatus.PENDING)
         .isPhysicSession(false)
         .build();

      paymentMutationRepository.save(paymentFactory.from(paymentEvent));
   }
}
