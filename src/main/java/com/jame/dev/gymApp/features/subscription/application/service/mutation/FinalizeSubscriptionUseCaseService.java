package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.FinalizeSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.CacheEvictSubscriptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinalizeSubscriptionUseCaseService implements FinalizeSubscriptionUseCase {
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final SubscriptionFactory subscriptionFactory;

   @Override
   @Transactional
   @CacheEvictSubscriptions
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      entityId = "#id",
      result = "#result"
   )
   public SubscriptionResponse finalize(long id) {
      final Instant updatedAt = Instant.now();
      log.info("[HIT]: finalize use case.");
      final SubscriptionEntity subscription = subscriptionQueryRepository.findById(id)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
      log.info("Subscription retrieved.");
      Optional.ofNullable(subscription.getPayments())
         .filter(Predicate.not(List::isEmpty))
         .map(List::getLast)
         .ifPresent(p -> {
            p.setStatus(PaymentStatus.FINALIZED);
            p.setUpdatedAt(updatedAt);
            p.setActive(false);
            log.info("check payment.");
         });

      subscription.setFinished(true);
      subscription.setUpdatedAt(updatedAt);
      log.info("subscription updated.");
      return subscriptionFactory.createFromEntity(subscriptionMutationRepository.save(subscription));
   }
}
