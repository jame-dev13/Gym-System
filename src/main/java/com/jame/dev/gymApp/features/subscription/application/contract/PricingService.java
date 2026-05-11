package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Optional;

public interface PricingService {
   List<PricingEntity> getAll();
   PricingEntity save(@NotNullObject final PricingEntity pricingEntity);
   Optional<PricingEntity> getById(@Positive final int id);
}