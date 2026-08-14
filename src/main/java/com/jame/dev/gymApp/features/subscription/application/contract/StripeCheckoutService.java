package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import org.springframework.security.core.Authentication;

public interface StripeCheckoutService {

    SubscriptionCheckoutResponse createCheckoutSessionFrom(final SubscriptionRequest request);
    SubscriptionCheckoutResponse createCheckoutSessionFrom(final Authentication authentication, final SubscriptionCurrentRequest request);
}
