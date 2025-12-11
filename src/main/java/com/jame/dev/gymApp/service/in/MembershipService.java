package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface MembershipService {
   List<MemberShipEntity> getAll();
   MemberShipEntity save(@NonNull final MemberShipEntity entity);
   Optional<MemberShipEntity> getByMembership(final @NonNull Membership membership);
}
