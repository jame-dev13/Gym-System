package com.jame.dev.gymApp.features.customer.application.usecases.mutation;

import org.springframework.security.core.Authentication;

public interface DeleteCurrentCustomerUseCase {
   void deleteCurrent(final Authentication authentication);
}
