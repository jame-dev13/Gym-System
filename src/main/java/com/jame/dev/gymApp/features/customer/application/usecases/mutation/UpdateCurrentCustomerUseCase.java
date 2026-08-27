package com.jame.dev.gymApp.features.customer.application.usecases.mutation;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;

public interface UpdateCurrentCustomerUseCase {
   CustomerResponse updateCurrent(
      final AuthPrincipal principal,
      final CustomerCurrentRequest request
   );
}
