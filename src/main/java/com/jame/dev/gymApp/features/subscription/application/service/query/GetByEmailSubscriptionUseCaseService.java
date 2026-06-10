package com.jame.dev.gymApp.features.subscription.application.service.query;

import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByEmailSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetByEmailSubscriptionUseCaseService implements GetByEmailSubscriptionUseCase {
    private final SubscriptionQueryRepository subscriptionQueryRepository;
    private final SubscriptionFactory subscriptionFactory;

    @Override
    public SubscriptionResponse getByEmail(String email) {
        return subscriptionQueryRepository.findByCustomerEmail(email)
            .map(subscriptionFactory::createFromEntity)
            .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not found for email: " + email));
    }
}
