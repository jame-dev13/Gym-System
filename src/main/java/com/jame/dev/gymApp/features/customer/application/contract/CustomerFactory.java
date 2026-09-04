package com.jame.dev.gymApp.features.customer.application.contract;

import com.jame.dev.gymApp.application.support.factories.Factory;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.dto.CustomerFactoryDtoInput;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;

public interface CustomerFactory extends Factory<CustomerEntity, CustomerResponse, CustomerFactoryDtoInput> {
   default CustomerEntity from(UserEntity user) {
      return CustomerEntity.builder()
         .user(user)
         .build();
   }
}
