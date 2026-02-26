package com.jame.dev.gymApp.model.dto.in;

import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PricingEntity;

import java.time.LocalDate;

public record SubscriptionFactoryDtoInput(
        @NotNullObject SubscriptionDtoInput subDto,
        @NotNullObject CustomerEntity customer,
        @NotNullObject PricingEntity pricing,
        @NotNullObject LocalDate startDate
) {
}
