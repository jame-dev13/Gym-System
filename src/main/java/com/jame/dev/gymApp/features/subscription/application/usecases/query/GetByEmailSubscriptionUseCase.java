package com.jame.dev.gymApp.features.subscription.application.usecases.query;

import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface GetByEmailSubscriptionUseCase {
    SubscriptionResponse getByEmail(final String email);
}
