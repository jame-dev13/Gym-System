package com.jame.dev.gymApp.factories.in;

import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.dto.in.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;

public non-sealed interface SubscriptionFactory extends Factory<
        SubscriptionEntity, SubscriptionDtoOutput, SubscriptionFactoryDtoInput> {
}
