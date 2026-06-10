package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.FinalizeSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.CacheEvictSubscriptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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
        final SubscriptionEntity subscription = subscriptionQueryRepository.findById(id)
            .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not Found."));
        subscription.setFinished(true);
        subscription.setUpdatedAt(Instant.now());
        final SubscriptionEntity subscriptionFinalized = subscriptionMutationRepository.save(subscription);
        return subscriptionFactory.createFromEntity(subscriptionFinalized);
    }
}
