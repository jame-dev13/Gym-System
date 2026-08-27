package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.domain.exception.UnrelatedDataAccessException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.UpdateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictSubsOnUpdate;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
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
   private final SubscriptionValidationRepository subscriptionValidationRepository;
   private final MembershipRepository membershipRepository;
   private final SubscriptionUpdater subscriptionUpdater;
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final SubscriptionFactory subscriptionFactory;

   @Override
   @Transactional
   @EvictSubsOnUpdate
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#request",
      entityId = "#id",
      result = "#result"
   )
   public SubscriptionResponse update(long id, SubscriptionRequest request) {
      if (!subscriptionValidationRepository.existsByIdAndCustomerEmail(id, request.customerEmail()))
         throw new UnrelatedDataAccessException("Trying to edit unrelated subscription data for non-owner: " + request.customerEmail());

      final SubscriptionEntity subscriptionEntity = subscriptionQueryRepository.findById(id)
         .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));

      final var membership = membershipRepository.findByMembership(request.membership())
         .orElseThrow(() -> new NotFoundException("Membership Not Found." + request.membership()));

      subscriptionUpdater.apply(subscriptionEntity, membership);

      Optional.ofNullable(subscriptionEntity.getPayments())
         .filter(Predicate.not(List::isEmpty))
         .map(List::getLast)
         .ifPresent(s -> s.setAmount(membership.getPrice()));

      return subscriptionFactory.createFromEntity(subscriptionMutationRepository.save(subscriptionEntity));
   }
}
