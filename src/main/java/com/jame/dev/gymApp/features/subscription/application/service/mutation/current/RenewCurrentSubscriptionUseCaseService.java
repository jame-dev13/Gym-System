package com.jame.dev.gymApp.features.subscription.application.service.mutation.current;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.RenewCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictCurrentOnUpdateSub;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class RenewCurrentSubscriptionUseCaseService implements RenewCurrentSubscriptionUseCase {
   private final SubscriptionValidator validator;
   private final MembershipRepository membershipRepository;
   private final SubscriptionUpdater subscriptionUpdater;
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final SubscriptionMapper subscriptionMapper;
   private final SubscriptionQueryRepository subscriptionQueryRepository;

   @Override
   @Transactional
   @EvictCurrentOnUpdateSub
   public SubscriptionResponse renew(AuthPrincipal principal, SubscriptionCurrentRequest request) {
      final String username = principal.username();
      final var subscription = subscriptionQueryRepository.findByCustomerEmail(username)
         .orElseThrow(() -> new NotFoundException("Subscription not found for customer: " + username));

      if (!validator.canRenewSubscription(subscription))
         throw new RenewSubscriptionException("Subscription cannot be renew now.");

      final var newMembership = membershipRepository.findByMembership(request.membership())
         .orElseThrow(() -> new NotFoundException("Membership not found for: " + request.membership()));

      subscriptionUpdater.applyRenew(subscription, newMembership);

      return subscriptionMapper.toDto(subscriptionMutationRepository.save(subscription));
   }
}
