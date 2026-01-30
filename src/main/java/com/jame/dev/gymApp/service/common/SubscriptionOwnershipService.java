package com.jame.dev.gymApp.service.common;

import org.springframework.security.core.Authentication;

public interface SubscriptionOwnershipService extends OwnershipService {
   boolean isOwner(String email, Authentication authentication);
}
