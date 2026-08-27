package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictSubsOnSave;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CreateSubscriptionUseCaseService implements CreateSubscriptionUseCase {
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final CustomerQueryRepository customerQueryRepository;
   private final MembershipRepository membershipRepository;
   private final SubscriptionValidationRepository subscriptionValidationRepository;
   private final SubscriptionFactory subscriptionFactory;

   @Override
   @Transactional
   @EvictSubsOnSave
   @AuditLog(
      action = AuditLogAction.INSERT,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#request",
      entityId = "#result.id",
      result = "#result"
   )
   public SubscriptionResponse create(SubscriptionRequest request) {
      log.info("[HIT]: Create subscription.");
      final CustomerEntity customer = customerQueryRepository.findByUserEmail(request.customerEmail())
         .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));

      if (subscriptionValidationRepository.existsByCustomer(customer)) {
         throw new AlreadyExistsException("There's a subscription linked to the customer with: " + request.customerEmail());
      }

      final MembershipEntity membershipEntity = membershipRepository.findByMembership(request.membership())
         .orElseThrow(() -> new NotFoundException("Membership Not Found: " + request.membership()));

      final SubscriptionEntity subscriptionEntity = subscriptionFactory.createFromInput(
         SubscriptionFactoryDtoInput.builder()
            .customer(customer)
            .membership(membershipEntity)
            .build());

      final SubscriptionEntity subscriptionSaved = subscriptionMutationRepository.save(subscriptionEntity);

      return subscriptionFactory.createFromEntity(subscriptionSaved);
   }
}
