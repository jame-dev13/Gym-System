package com.jame.dev.gymApp.features.customer.application.usecases.query;

import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import org.springframework.security.core.Authentication;

public interface GetCurrentCustomerUseCase {
   CustomerResponse getCurrent(final Authentication authentication);
}
