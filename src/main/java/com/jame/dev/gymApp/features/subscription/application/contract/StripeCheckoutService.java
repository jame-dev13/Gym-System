package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.api.request.CheckoutRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;

public interface StripeCheckoutService {

    SubscriptionCheckoutResponse createCheckoutSession(final CheckoutRequest request);
}
