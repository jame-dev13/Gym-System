package com.jame.dev.gymApp.features.subscription.application.dto;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import lombok.Builder;

@Builder
public record SubscriptionFactoryDtoInput(
        @NotNullObject CustomerEntity customer,
        @NotNullObject MembershipEntity membership
) {
}
