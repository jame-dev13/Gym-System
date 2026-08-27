package com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface CreateCurrentSubscriptionUseCase {
   SubscriptionResponse create(final AuthPrincipal principal, final SubscriptionCurrentRequest request);
}
