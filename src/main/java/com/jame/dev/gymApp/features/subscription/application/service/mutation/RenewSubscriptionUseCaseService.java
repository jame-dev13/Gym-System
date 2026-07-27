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
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.RenewSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.CacheEvictSubscriptions;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class RenewSubscriptionUseCaseService implements RenewSubscriptionUseCase {
    private final SubscriptionValidator validator;
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
        input = "#input",
        entityId = "#id",
        result = "#result"
    )
    public SubscriptionResponse renew(long id, SubscriptionRequest input) {
        final SubscriptionEntity subscriptionEntity = validator.validateOnRenew(id, input);

        final PricingEntity pricing = pricingRepository.findByMemberShipEntity_Membership(input.membership())
            .orElseThrow(() -> new PricingNotFoundException("Pricing not found."));

        subscriptionUpdater.applyRenew(subscriptionEntity, pricing);
        final SubscriptionEntity subscriptionRenewed = subscriptionMutationRepository.save(subscriptionEntity);

        return subscriptionFactory.createFromEntity(subscriptionRenewed);
    }
}
