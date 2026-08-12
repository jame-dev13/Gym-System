package com.jame.dev.gymApp.features.customer.application.usecases.mutation;

import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import org.springframework.security.core.Authentication;

public interface UpdateCurrentCustomerUseCase {
   CustomerResponse updateCurrent(
      final Authentication authentication,
      final CustomerCurrentRequest request
   );
}
