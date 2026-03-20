package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;

public interface SubscriptionOwnershipService extends OwnershipService {
   boolean isOwner(final SubscriptionDtoInput input, @NonNull Authentication authentication);
}
