package com.jame.dev.gymApp.application.contract;

import lombok.NonNull;
import org.springframework.security.core.Authentication;

public interface OwnershipService {
   boolean isOwner(final long id, final @NonNull Authentication authentication);
   boolean isOwner(final String customerEmail, @NonNull Authentication authentication);
}
