package com.jame.dev.gymApp.features.subscription.application.service.query;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByIdSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetByIdSubscriptionUseCaseService implements GetByIdSubscriptionUseCase {
    private final SubscriptionQueryRepository subscriptionQueryRepository;
    private final SubscriptionFactory subscriptionFactory;

    @Override
    @Cacheable(value = CacheValues.SUBSCRIPTION, key = "#id")
    public SubscriptionResponse getById(long id) {
        return subscriptionQueryRepository.findById(id)
            .map(subscriptionFactory::createFromEntity)
            .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not found."));
    }
}
