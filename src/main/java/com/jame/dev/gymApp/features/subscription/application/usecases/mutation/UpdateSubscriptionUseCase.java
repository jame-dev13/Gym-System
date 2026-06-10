package com.jame.dev.gymApp.features.subscription.application.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface UpdateSubscriptionUseCase {
    SubscriptionResponse update(final long id, final SubscriptionRequest request);
}
