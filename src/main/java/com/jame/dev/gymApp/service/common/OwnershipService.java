package com.jame.dev.gymApp.service.common;

import lombok.NonNull;
import org.springframework.security.core.Authentication;

public interface OwnershipService <ID>{
   boolean isOwner(final ID id, final @NonNull Authentication authentication);
}
