package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface PricingService {
   List<PricingEntity> getAll();
   PricingEntity save(@NonNull final PricingEntity pricingEntity);
   Optional<PricingEntity> getById(final int id);
   Optional<PricingEntity> getByMembership(final Membership membership);
}