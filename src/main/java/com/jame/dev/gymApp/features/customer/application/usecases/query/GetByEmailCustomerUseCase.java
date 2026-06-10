package com.jame.dev.gymApp.features.customer.application.usecases.query;

import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;

public interface GetByEmailCustomerUseCase {
    CustomerResponse getByEmail(final String email);
}
