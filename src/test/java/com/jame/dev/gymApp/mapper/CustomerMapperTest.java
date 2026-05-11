package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.application.support.mapper.UserMapper;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class CustomerMapperTest {
   private final RoleMapper roleMapper = new RoleMapperImpl();
   private final UserMapper userMapper = new UserMapperImpl(roleMapper);
   private final CustomerMapper customerMapper = new CustomerMapperImpl(userMapper);

   private final UserEntity testUser = UserEntity.builder()
           .name("user")
           .email("user@maul.com")
           .password("12bf4")
           .roles(Set.of(new RoleEntity(null, Role.USER)))
           .build();

   @Test
   void toDto() {
      CustomerResponse dto = customerMapper.toDto(
              CustomerEntity.builder()
                      .user(testUser)
                      .phoneContact("128133")
                      .build());
      Assertions.assertNotNull(dto, "Should not be null.");
   }

   @Test
   void toEntity() {
      CustomerRequest dto = new CustomerRequest("any@mail.com", "347293");
      CustomerEntity entity = customerMapper.toEntity(dto, testUser);
      Assertions.assertNotNull(entity, "Should not be null.");
   }
}