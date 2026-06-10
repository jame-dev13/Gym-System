package com.jame.dev.gymApp.features.subscription.application.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;

public interface FinalizeSubscriptionUseCase {
    SubscriptionResponse finalize(final long id);
}
