package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.shared.enums.Role;
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
      CustomerDtoOutput dto = customerMapper.toDto(
              CustomerEntity.builder()
                      .user(testUser)
                      .phoneContact("128133")
                      .build());
      Assertions.assertNotNull(dto, "Should not be null.");
   }

   @Test
   void toEntity() {
      CustomerDtoInput dto = new CustomerDtoInput("any@mail.com", "347293");
      CustomerEntity entity = customerMapper.toEntity(dto, testUser);
      Assertions.assertNotNull(entity, "Should not be null.");
   }
}