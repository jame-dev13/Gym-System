package com.jame.dev.gymApp.features.customer.application.usecases.mutation;

import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;

public interface RecoverCustomerUseCase {
    CustomerResponse recover(final RecoveryRequest request);
}
