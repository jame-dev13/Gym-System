package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface MembershipService {
   List<MembershipEntity> getAll();
   MembershipEntity save(@NonNull final MembershipEntity entity);
   Optional<MembershipEntity> getByMembership(final @NonNull Membership membership);
}
