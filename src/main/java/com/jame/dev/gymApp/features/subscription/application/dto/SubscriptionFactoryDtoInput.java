package com.jame.dev.gymApp.features.subscription.application.dto;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;

import java.time.LocalDate;

public record SubscriptionFactoryDtoInput(
        @NotNullObject SubscriptionRequest subDto,
        @NotNullObject CustomerEntity customer,
        @NotNullObject PricingEntity pricing,
        @NotNullObject LocalDate startDate
) {
}
