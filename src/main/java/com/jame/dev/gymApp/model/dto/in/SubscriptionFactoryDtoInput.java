package com.jame.dev.gymApp.model.dto.in;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import lombok.NonNull;

import java.time.LocalDate;

public record SubscriptionFactoryDtoInput(
        @NonNull SubscriptionDtoInput subDto,
        @NonNull CustomerEntity customer,
        @NonNull PricingEntity pricing,
        @NonNull LocalDate startDate
) {
}
