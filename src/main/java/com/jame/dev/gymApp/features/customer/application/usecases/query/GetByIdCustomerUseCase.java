package com.jame.dev.gymApp.features.customer.application.usecases.query;

import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;

public interface GetByIdCustomerUseCase {
    CustomerResponse getById(final long id);
}
