package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;

public interface CustomerService extends CRUDServiceServicePut<CustomerEntity, CustomerDtoInput, Long> {
}
