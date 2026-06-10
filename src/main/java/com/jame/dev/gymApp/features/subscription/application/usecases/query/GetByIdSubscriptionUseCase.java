package com.jame.dev.gymApp.features.subscription.application.usecases.query;

import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface GetByIdSubscriptionUseCase {
    SubscriptionResponse getById(final long id);
}
