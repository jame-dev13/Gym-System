package com.jame.dev.gymApp.features.customer.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.application.contract.BaseService;
import com.jame.dev.gymApp.application.contract.EmailIdentifiable;
import jakarta.validation.constraints.Positive;

import java.util.Optional;

public interface CustomerService extends
   BaseService<CustomerResponse, CustomerRequest>,
        EmailIdentifiable<CustomerEntity> {
   Optional<CustomerEntity> getUserByEmail(@EmailValid final String email);
   boolean exitsByIdAndCustomerEmail(@Positive final long id, @EmailValid final String email);
}
