package com.jame.dev.gymApp.features.subscription.application.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface RenewSubscriptionUseCase {
    SubscriptionResponse renew(final long id, final SubscriptionRequest input);
}
