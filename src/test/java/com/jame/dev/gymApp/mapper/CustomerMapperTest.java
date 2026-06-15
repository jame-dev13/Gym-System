package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapperImpl;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class CustomerMapperTest {
   private final CustomerMapper customerMapper = new CustomerMapperImpl();

   @Test
   void toDto() {
      CustomerResponse dto = customerMapper.toDto(new CustomerEntity());
      Assertions.assertNotNull(dto, "Should not be null.");
   }

   @Test
   void toEntity() {
      CustomerRequest dto = new CustomerRequest("any@mail.com", "347293");
      CustomerEntity entity = customerMapper.toEntity(dto, new com.jame.dev.gymApp.features.user.domain.model.UserEntity());
      Assertions.assertNotNull(entity, "Should not be null.");
   }
}