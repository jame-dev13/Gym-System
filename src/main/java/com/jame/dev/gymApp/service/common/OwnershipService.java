package com.jame.dev.gymApp.service.common;

import lombok.NonNull;
import org.springframework.security.core.Authentication;

public interface OwnershipService{
   boolean isOwner(long id, final @NonNull Authentication authentication);
}
