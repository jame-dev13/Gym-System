package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;

import java.util.Optional;

public interface CustomerService extends CRUDServiceServicePut<CustomerEntity, CustomerDtoInput, Long> {
   Optional<CustomerEntity> getUserByEmail(String email);
}
