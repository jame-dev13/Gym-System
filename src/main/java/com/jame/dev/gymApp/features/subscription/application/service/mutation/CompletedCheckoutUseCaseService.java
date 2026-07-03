package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.domain.exception.EntityNotFoundException;
import com.jame.dev.gymApp.features.metrics.infrastructure.annotations.EvictPaymentMetrics;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CompletedCheckoutUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentQueryRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompletedCheckoutUseCaseService implements CompletedCheckoutUseCase {
   private final PaymentMutationRepository paymentMutationRepository;
   private final PaymentQueryRepository paymentQueryRepository;
   private final SubscriptionMutationRepository subscriptionMutationRepository;

   @Override
   @Transactional
   @Caching(
      evict = {
         @CacheEvict(value = CacheValues.SUBSCRIPTIONS, allEntries = true),
         @CacheEvict(value = CacheValues.SUBSCRIPTION, allEntries = true),
         @CacheEvict(value = CacheValues.PAYMENTS, allEntries = true)
      }
   )
   @EvictPaymentMetrics
   public void execute(CompletedCheckoutEvent event) {
      final PaymentEntity paymentEntity = paymentQueryRepository.findByStripeSessionId(event.stripeSessionId())
         .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

      paymentEntity.setStatus(PaymentStatus.COMPLETED);
      paymentEntity.setUpdatedAt(Instant.now());

      final PaymentEntity paymentSaved = paymentMutationRepository.save(paymentEntity);
      final SubscriptionEntity subscription = paymentSaved.getSubscription();
      subscription.setPaid(true);
      subscriptionMutationRepository.save(subscription);

      log.info("Checkout completed: session={}, subscription={}, customer={}",
         event.stripeSessionId(), subscription.getId(), event.customerEmail());
      log.info("Subscription is Paid: {}", subscription.isPaid());
      log.info("Payment status: {}", paymentSaved.getStatus());
   }
}
