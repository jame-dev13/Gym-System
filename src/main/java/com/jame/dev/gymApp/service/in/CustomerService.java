package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.service.common.CRUDService;

import java.util.Optional;

public interface CustomerService extends CRUDService<CustomerEntity, CustomerDtoInput> {
   Optional<UserEntity> getUserAssociatedById(final long id);
}
