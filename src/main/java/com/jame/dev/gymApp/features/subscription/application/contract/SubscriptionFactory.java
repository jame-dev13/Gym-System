package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.application.support.factories.Factory;

public non-sealed interface SubscriptionFactory extends Factory<
        SubscriptionEntity, SubscriptionResponse, SubscriptionFactoryDtoInput> {
}
