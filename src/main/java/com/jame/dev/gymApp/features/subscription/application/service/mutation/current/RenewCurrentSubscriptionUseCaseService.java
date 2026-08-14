package com.jame.dev.gymApp.features.subscription.application.service.mutation.current;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
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
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class RenewCurrentSubscriptionUseCaseService implements RenewCurrentSubscriptionUseCase {
   private final SubscriptionValidator validator;
   private final PricingRepository pricingRepository;
   private final SubscriptionUpdater subscriptionUpdater;
   private final SubscriptionMutationRepository subscriptionMutationRepository;
   private final SubscriptionMapper subscriptionMapper;
   private final SubscriptionQueryRepository subscriptionQueryRepository;
   private final IdentityExtractorService identityExtractorService;

   @Override
   @Transactional
   @EvictCurrentOnUpdateSub
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.SUBSCRIPTION,
      input = "#authentication",
      result = "#result"
   )
   public SubscriptionResponse renew(Authentication authentication, SubscriptionCurrentRequest request) {
      final String email = identityExtractorService.extract(authentication);
      final var subscription = subscriptionQueryRepository.findByCustomerEmail(email)
         .orElseThrow(() -> new NotFoundException("Subscription not found for customer: " + email));

      if (!validator.canRenewSubscription(subscription))
         throw new RenewSubscriptionException("Subscription cannot be renew now.");

      final var newPricing = pricingRepository.findByMemberShipEntity_Membership(request.membership())
         .orElseThrow(() -> new NotFoundException("Pricing not found exception."));

      subscriptionUpdater.applyRenew(subscription, newPricing);

      return subscriptionMapper.toDto(subscriptionMutationRepository.save(subscription));
   }
}
