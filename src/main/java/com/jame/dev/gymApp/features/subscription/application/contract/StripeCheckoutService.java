package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;

public interface StripeCheckoutService {

    SubscriptionCheckoutResponse createCheckoutSessionFrom(final SubscriptionRequest request);
    SubscriptionCheckoutResponse createCheckoutSessionFrom(final AuthPrincipal principal, final SubscriptionCurrentRequest request);
}
