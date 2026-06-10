package com.jame.dev.gymApp.features.customer.application.usecases.mutation;

import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;

public interface UpdateCustomerUseCase {
    CustomerResponse update(final long id, final CustomerRequest request);
}
