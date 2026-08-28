package com.jame.dev.gymApp.features.subscription.application.service.mutation.current;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.CreateCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictSubsOnSave;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class CreateCurrentSubscriptionUseCaseService implements CreateCurrentSubscriptionUseCase {
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final CustomerQueryRepository customerQueryRepository;
   private final MembershipRepository membershipRepository;
   private final SubscriptionValidationRepository subscriptionValidationRepository;
   private final SubscriptionFactory subscriptionFactory;

   @Override
   @Transactional
   @EvictSubsOnSave
   public SubscriptionResponse create(AuthPrincipal principal, SubscriptionCurrentRequest request) {
      final String username = principal.username();
      final CustomerEntity customer = customerQueryRepository.findByUserEmail(username)
         .orElseThrow(() -> new NotFoundException("Customer Not Found for: " + username));

      if (subscriptionValidationRepository.existsByCustomer(customer)) {
         throw new AlreadyExistsException("There's a subscription linked to the customer with: " + username);
      }

      final var membership = membershipRepository.findByMembership(request.membership())
         .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));

      final SubscriptionEntity subscriptionEntity = subscriptionFactory.createFromInput(
         new SubscriptionFactoryDtoInput(customer, membership));

      final SubscriptionEntity subscriptionSaved = subscriptionMutationRepository.save(subscriptionEntity);

      return subscriptionFactory.createFromEntity(subscriptionSaved);
   }
}
