package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.metrics.infrastructure.annotations.EvictPaymentMetrics;
import com.jame.dev.gymApp.features.subscription.api.request.PaymentRequest;
import com.jame.dev.gymApp.features.subscription.application.support.factory.PaymentFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreatePaymentUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.StripeSessionPaymentEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CreatePaymentUseCaseService implements CreatePaymentUseCase {
   private final PaymentMutationRepository paymentMutationRepository;
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final PaymentFactory paymentFactory;

   @Override
   @Transactional
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
