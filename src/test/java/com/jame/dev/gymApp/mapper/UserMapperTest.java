package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapperTest {
   private RoleMapper roleMapper = new RoleMapperImpl();
   private UserMapper userMapper = new UserMapperImpl(roleMapper);

   @Test
   @DisplayName("To Dto")
   void toDto(){
      UserEntity user = UserEntity.builder()
              .id(1L)
              .name("userEntity")
              .email("userEntity@mail.com")
              .password("1223432")
              .roles(Set.of(new RoleEntity(1, Role.USER)))
              .active(true)
              .build();
      UserDtoOutput dto = userMapper.toDto(user);
      Assertions.assertNotNull(dto, "Should not be null.");
   }

   @Test
   @DisplayName("To Entity")
   void toEntity(){
      UserDtoInput dto = UserDtoInput.builder()
              .name("dtoname")
              .email("dto@mail")
              .password("324524")
              .roles(Set.of(Role.USER))
              .active(true)
              .build();
      Set<RoleEntity> entitySet = dto.roles()
              .stream()
              .map(roleMapper::toEntity)
              .collect(Collectors.toSet());
      Assertions.assertNotNull(entitySet, "Should not be null.");
      Assertions.assertFalse(entitySet.isEmpty(), "Should not be empty.");
   }
}

