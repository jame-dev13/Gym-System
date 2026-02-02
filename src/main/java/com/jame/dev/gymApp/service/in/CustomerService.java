package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;

import java.util.Optional;

public interface CustomerService extends
        CRUDServiceServicePut<CustomerEntity, CustomerDtoInput, Long>,
        EmailIdentifiable<CustomerEntity> {
   Optional<CustomerEntity> getUserByEmail(String email);
   boolean exitsByIdAndCustomerEmail(long id, String email);
}
