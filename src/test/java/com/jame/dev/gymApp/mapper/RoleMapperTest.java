package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapperImpl;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleMapperTest {

   @Mock
   private RoleRepository roleRepository;

   private final RoleMapper roleMapper = new RoleMapperImpl() {
   };

   @Captor
   private ArgumentCaptor<Role> roleCaptor;

   private final Role userRole = Role.USER;
   private final Role adminRole = Role.ADMIN;

   private final RoleEntity roleUserEntity = new RoleEntity(1, userRole);
   private final RoleEntity roleAdminEntity = new RoleEntity(2, adminRole);

   private final Set<RoleEntity> entityRoles = Set.of(roleUserEntity, roleAdminEntity);
   private final Set<Role> roles = Set.of(userRole, adminRole);

   @Test
   @DisplayName("To Role")
   void toRole() {
      final Role role = roleMapper.toDto(this.roleAdminEntity);
      assertNotNull(role, "Should not be null.");
      assertEquals(role, this.roleAdminEntity.getRole(), "Should be the same.");
   }

   @Test
   @DisplayName("To Role Set")
   void toRoleSet() {
      final Set<Role> roleSet = entityRoles.stream()
              .map(roleMapper::toDto)
              .collect(Collectors.toSet());
      assertNotNull(roleSet, "Should not be null.");
      assertFalse(roles.isEmpty(), "Should not be empty");
      assertTrue(roles.contains(Role.USER), "Should contains Role.USER");
      assertTrue(roles.contains(Role.ADMIN), "Should contains Role.ADMIN");
   }

   @Test
   @DisplayName("To Entity")
   void toEntity() {
      when(roleRepository.getReferenceByRole(adminRole)).thenReturn(this.roleAdminEntity);

      final RoleEntity entity = roleMapper.toEntity(this.adminRole, roleRepository);

      verify(roleRepository, times(1)).getReferenceByRole(adminRole);

      assertNotNull(entity, "Should not be null.");
      assertEquals(roleAdminEntity, entity, "Role entities should be equals.");
      assertSame(roleAdminEntity, entity, "Should be the same object");
   }

   @Test
   @DisplayName("Should return an RoleEntity Set")
   void toEntitySet() {
      when(roleRepository.getReferenceByRole(eq(Role.USER)))
              .thenReturn(roleUserEntity);
      when(roleRepository.getReferenceByRole(eq(Role.ADMIN)))
              .thenReturn(roleAdminEntity);

      Set<RoleEntity> roleEntitySet = roles.stream()
              .map(r -> roleMapper.toEntity(r, roleRepository))
              .collect(Collectors.toSet());

      verify(roleRepository, atMost(2)).getReferenceByRole(roleCaptor.capture());

      assertNotNull(roleEntitySet, "Role set should not be null.");
      assertEquals(entityRoles, roleEntitySet, "Entities set should be equals.");
   }
}
