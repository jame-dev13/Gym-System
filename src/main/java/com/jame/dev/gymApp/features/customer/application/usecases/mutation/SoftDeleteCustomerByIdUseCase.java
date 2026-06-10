package com.jame.dev.gymApp.features.customer.application.usecases.mutation;

public interface SoftDeleteCustomerByIdUseCase {
    void softDeleteById(final long id);
}
