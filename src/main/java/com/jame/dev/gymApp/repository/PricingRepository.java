package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingRepository extends JpaRepository<@NonNull PricingEntity, @NonNull Integer> {
   Optional<PricingEntity> findByMemberShipEntity_Membership(@NonNull final Membership membership);
}
