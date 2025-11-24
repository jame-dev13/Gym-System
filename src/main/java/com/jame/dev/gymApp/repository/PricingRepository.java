package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.PricingEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingRepository extends JpaRepository<@NonNull PricingEntity, @NonNull Integer> {
}
