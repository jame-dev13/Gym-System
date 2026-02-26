package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.entity.PricingEntity;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Optional;

public interface PricingService {
   List<PricingEntity> getAll();
   PricingEntity save(@NotNullObject final PricingEntity pricingEntity);
   Optional<PricingEntity> getById(@Positive final int id);
}