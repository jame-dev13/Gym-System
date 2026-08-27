package com.jame.dev.gymApp.features.customer.application.usecases.query;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;

public interface GetCurrentCustomerUseCase {
   CustomerResponse getCurrent(final AuthPrincipal principal);
}
