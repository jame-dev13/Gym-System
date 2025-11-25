package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.service.common.CRUDStaticService;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;

import java.util.Optional;

public interface MembershipService extends CRUDStaticService<MemberShipEntity> {
   Optional<MemberShipEntity> getByMembership(final @NonNull Membership membership);
}
