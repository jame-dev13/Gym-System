package com.jame.dev.gymApp.features.subscription.application.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.api.request.PaymentRequest;

public interface CreatePaymentUseCase {
   void create(final PaymentRequest paymentRequest);
}
