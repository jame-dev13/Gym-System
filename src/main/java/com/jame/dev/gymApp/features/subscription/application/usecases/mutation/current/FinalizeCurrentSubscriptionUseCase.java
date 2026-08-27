package com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface FinalizeCurrentSubscriptionUseCase {
   SubscriptionResponse finalizeCurrent(final AuthPrincipal principal);
}
