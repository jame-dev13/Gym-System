package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.domain.model.MemberShipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface MembershipService {
   List<MemberShipEntity> getAll();
   MemberShipEntity save(@NonNull final MemberShipEntity entity);
   Optional<MemberShipEntity> getByMembership(final @NonNull Membership membership);
}
