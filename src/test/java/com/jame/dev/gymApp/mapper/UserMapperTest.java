package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
   @Mock
   private RoleRepository roleRepository;

   private final RoleMapper roleMapper = new RoleMapperImpl() {};
   private final UserMapper userMapper = new UserMapperImpl(roleMapper) {};

   private final RoleEntity userRoleEntity = new RoleEntity(1, Role.USER);

   @Test
   @DisplayName("Should map to Dto")
   void toDto(){
      UserEntity user = UserEntity.builder()
              .name("userEntity")
              .email("userEntity@mail.com")
              .password("1223432")
              .roles(Set.of(userRoleEntity))
              .provider(AuthProvider.LOCAL)
              .build();
      UserDtoOutput dto = userMapper.toDto(user);
      assertNotNull(dto, "Should not be null.");
   }

   @Test
   @DisplayName("Should map To Entity")
   void toEntity(){
      final UserDtoInput dto = UserDtoInput.builder()
              .name("dtoname")
              .email("dto@mail")
              .password("324524")
              .roles(Set.of(Role.USER))
              .authProvider(AuthProvider.LOCAL)
              .build();
      when(roleRepository.findByRole(any(Role.class))).thenReturn(Optional.of(userRoleEntity));

      final Set<RoleEntity> entitySet = dto.roles()
              .stream()
              .map(r -> roleMapper.toEntity(r, roleRepository))
              .collect(Collectors.toSet());
      final UserEntity userEntity = userMapper.toEntity(dto, entitySet);

      verify(roleRepository, atLeastOnce()).findByRole(any(Role.class));
      verifyNoMoreInteractions(roleRepository);

      assertNotNull(entitySet, "Should not be null.");
      assertFalse(entitySet.isEmpty(), "Should not be empty.");
      assertNotNull(userEntity, "UserEntity should not be null.");
   }
}

