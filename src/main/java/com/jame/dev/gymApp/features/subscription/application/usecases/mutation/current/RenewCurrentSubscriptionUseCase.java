package com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import org.springframework.security.core.Authentication;

public interface RenewCurrentSubscriptionUseCase {
   SubscriptionResponse renew(final Authentication authentication, final SubscriptionCurrentRequest request);
}
