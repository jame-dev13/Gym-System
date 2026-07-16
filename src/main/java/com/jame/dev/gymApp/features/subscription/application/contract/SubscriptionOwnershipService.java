package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.infrastructure.security.owner.OwnershipService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;

public interface SubscriptionOwnershipService extends OwnershipService {
   boolean isOwner(final SubscriptionRequest input, @NonNull Authentication authentication);
}
