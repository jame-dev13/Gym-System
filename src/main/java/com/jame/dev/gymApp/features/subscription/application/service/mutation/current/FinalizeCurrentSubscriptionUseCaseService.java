package com.jame.dev.gymApp.features.subscription.application.service.mutation.current;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.FinalizeCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictCurrentOnUpdateSub;
import com.jame.dev.gymApp.features.subscription.infrastructure.publisher.SubscriptionMutationEventPublisher;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
@CheckLockProcess
public class FinalizeCurrentSubscriptionUseCaseService implements FinalizeCurrentSubscriptionUseCase {
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final SubscriptionFactory subscriptionFactory;
   private final SubscriptionMutationEventPublisher subscriptionMutationEventPublisher;
   private final IdentityExtractorService identityExtractorService;

   @Override
   @Transactional
   @AuditLog(
      entityType = AuditLogEntityType.SUBSCRIPTION,
      action = AuditLogAction.UPDATE,
      input = "#authentication",
      result = "#result"
   )
   @EvictCurrentOnUpdateSub
   public SubscriptionResponse finalizeCurrent(Authentication authentication) {
      log.info("Hit finalizeCurrent");
      final String email = identityExtractorService.extract(authentication);
      final SubscriptionEntity subscription = subscriptionQueryRepository.findByCustomerEmail(email)
         .orElseThrow(() -> new NotFoundException("Subscription Not Found."));

      Optional.ofNullable(subscription.getPayments())
         .filter(Predicate.not(List::isEmpty))
         .map(List::getLast)
         .ifPresent(p -> p.setStatus(PaymentStatus.FINALIZED));

      subscription.setStatus(SubscriptionStatus.FINALIZED);
      final SubscriptionEntity subscriptionEntity = subscriptionMutationRepository.save(subscription);
      log.info("Subscription status: {}", subscriptionEntity.getStatus());
      subscriptionMutationEventPublisher.publishSubscriptionFinalized(subscriptionEntity);
      return subscriptionFactory.createFromEntity(subscriptionEntity);
   }
}
