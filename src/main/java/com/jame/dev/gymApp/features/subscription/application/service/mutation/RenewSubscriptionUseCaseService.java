package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.RenewSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.annotations.EvictSubsOnUpdate;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class RenewSubscriptionUseCaseService implements RenewSubscriptionUseCase {
    private final SubscriptionValidator validator;
    private final MembershipRepository membershipRepository;
    private final SubscriptionUpdater subscriptionUpdater;
    private final SubscriptionMutationRepository subscriptionMutationRepository;
    private final SubscriptionFactory subscriptionFactory;

    @Override
    @Transactional
    @EvictSubsOnUpdate
    public SubscriptionResponse renew(long id, SubscriptionRequest input) {
        final SubscriptionEntity subscriptionEntity = validator.validateOnRenew(id, input);

        final MembershipEntity membership = membershipRepository.findByMembership(input.membership())
            .orElseThrow(() -> new NotFoundException("Membership not found: " + input.membership()));

        subscriptionUpdater.applyRenew(subscriptionEntity, membership);
        final SubscriptionEntity subscriptionRenewed = subscriptionMutationRepository.save(subscriptionEntity);

        return subscriptionFactory.createFromEntity(subscriptionRenewed);
    }
}
