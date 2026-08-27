package com.jame.dev.gymApp.features.customer.application.usecases.mutation;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;

public interface DeleteCurrentCustomerUseCase {
   void deleteCurrent(final AuthPrincipal principal);
}
