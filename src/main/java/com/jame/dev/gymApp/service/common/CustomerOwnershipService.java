package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;

public interface CustomerOwnershipService extends OwnershipService {
   boolean isOwner(final CustomerDtoInput input, @NonNull Authentication authentication);
}
