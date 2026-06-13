package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.api.request.CheckoutRequest;
import com.jame.dev.gymApp.features.subscription.api.response.CheckoutResponse;

public interface StripeCheckoutService {

    CheckoutResponse createCheckoutSession(final CheckoutRequest request, final String customerEmail);
}
