package com.jame.dev.gymApp.features.customer.application.contract;

import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.application.contract.OwnershipService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;

public interface CustomerOwnershipService extends OwnershipService {
   boolean isOwner(final CustomerRequest input, @NonNull Authentication authentication);
}
