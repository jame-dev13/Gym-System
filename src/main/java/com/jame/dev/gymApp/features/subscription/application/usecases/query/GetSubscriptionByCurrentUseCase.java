package com.jame.dev.gymApp.features.subscription.application.usecases.query;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface GetSubscriptionByCurrentUseCase {
   SubscriptionResponse getCurrent(final AuthPrincipal principal);
}
