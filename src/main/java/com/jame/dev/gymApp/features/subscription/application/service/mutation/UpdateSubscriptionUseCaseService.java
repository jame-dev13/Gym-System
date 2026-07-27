package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.metrics.infrastructure.annotations.EvictEarningMetrics;
import com.jame.dev.gymApp.features.metrics.infrastructure.annotations.EvictSubscriptionMetrics;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.UpdateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.CacheEvictSubscriptions;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class UpdateSubscriptionUseCaseService implements UpdateSubscriptionUseCase {
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final PricingRepository pricingRepository;
   private final SubscriptionUpdater subscriptionUpdater;
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final SubscriptionFactory subscriptionFactory;

   @Override
   @Transactional
   @CacheEvictSubscriptions
   @EvictEarningMetrics
   @EvictSubscriptionMetrics
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#request",
      entityId = "#id",
      result = "#result"
   )
   public SubscriptionResponse update(long id, SubscriptionRequest request) {
      final SubscriptionEntity subscriptionEntity = subscriptionQueryRepository.findById(id)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      final PricingEntity pricingEntity = pricingRepository.findByMemberShipEntity_Membership(request.membership())
         .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));

      subscriptionUpdater.apply(subscriptionEntity, pricingEntity);

      Optional.ofNullable(subscriptionEntity.getPayments())
         .filter(Predicate.not(List::isEmpty))
         .map(List::getLast)
         .ifPresent(s -> s.setAmount(pricingEntity.getPrice()));

      return subscriptionFactory.createFromEntity(subscriptionMutationRepository.save(subscriptionEntity));
   }
}
