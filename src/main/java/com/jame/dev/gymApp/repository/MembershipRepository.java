package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<@NonNull MemberShipEntity, @NonNull Integer> {
   Optional<MemberShipEntity> findByMembership(@NonNull final Membership membership);
}
