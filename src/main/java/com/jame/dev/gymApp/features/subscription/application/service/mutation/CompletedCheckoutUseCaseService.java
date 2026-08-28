package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.domain.exception.EntityNotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.application.dto.CompletedCheckoutResult;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CompletedCheckoutUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentQueryRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictOnCompleteCheckout;
import com.jame.dev.gymApp.features.subscription.infrastructure.publisher.SubscriptionMutationEventPublisher;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CompletedCheckoutUseCaseService implements CompletedCheckoutUseCase {
   private final PaymentMutationRepository paymentMutationRepository;
   private final PaymentQueryRepository paymentQueryRepository;
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final SubscriptionMutationEventPublisher subscriptionMutationEventPublisher;
   private final SubscriptionMapper subscriptionMapper;

   private final Function<SubscriptionStatus, SubscriptionStatus> subscriptionStatusResolver =
      status -> switch (status) {
         case ON_RENEWAL -> SubscriptionStatus.RENEWED;
         case NOT_PAID -> SubscriptionStatus.PAID;
         default -> throw new IllegalArgumentException(status + " status unreachable at this point.");
      };

   @Override
   @Transactional
   @EvictOnCompleteCheckout
   @AuditLog(
      entityType = AuditLogEntityType.SUBSCRIPTION,
      action = AuditLogAction.CHECKOUT,
      input = "#event",
      result = "#result"
   )
   public CompletedCheckoutResult execute(CompletedCheckoutEvent event) {
      final PaymentEntity paymentEntity = paymentQueryRepository.findByStripeSessionId(event.stripeSessionId())
         .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

      paymentEntity.setStatus(PaymentStatus.COMPLETED);

      final PaymentEntity paymentSaved = paymentMutationRepository.save(paymentEntity);
      final SubscriptionEntity subscription = paymentSaved.getSubscription();
      subscription.setStatus(subscriptionStatusResolver.apply(subscription.getStatus()));

      final SubscriptionEntity subscriptionSaved = subscriptionMutationRepository.save(subscription);

      log.info("Payment status: {}", paymentSaved.getStatus());

      subscriptionMutationEventPublisher.publishSubscriptionMutated(subscriptionSaved);

      final var subscriptionRes = subscriptionMapper.toDto(subscriptionSaved);

      return new CompletedCheckoutResult(paymentSaved.getStatus(), subscriptionRes);
   }
}
