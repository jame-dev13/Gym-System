package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class RoleMapperTest {
   private final RoleMapper roleMapper = new RoleMapperImpl();
   private final RoleEntity entity = new RoleEntity(1, Role.USER);
   private final Role role = Role.ADMIN;

   private final Set<Role> roles = Set.of(Role.USER, Role.ADMIN);
   private final Set<RoleEntity> entities = Set.of(entity, new RoleEntity(2, Role.ADMIN));

   @Test
   @DisplayName("To Role")
   void toRole() {
      final Role role = roleMapper.toDto(this.entity);
      assertAll("Not null and Roles are the same.",
              () -> assertNotNull(role, "Should not be null."),
              () -> assertEquals(role, entity.getRole(), "Should be the same."));
   }

   @Test
   @DisplayName("To Role Set")
   void toRoleSet() {
      final Set<Role> roleSet = entities.stream()
              .map(roleMapper::toDto)
              .collect(Collectors.toSet());
      assertAll("Not null, not empty and contains both existing Roles.",
              () -> assertNotNull(roleSet, "Should not be null."),
              () -> assertFalse(roles.isEmpty(), "Should not be empty"),
              () -> assertTrue(roles.contains(Role.USER), "Should contains Role.USER"),
              () -> assertTrue(roles.contains(Role.ADMIN), "Should contains Role.ADMIN"));
   }

   @Test
   @DisplayName("To Entity")
   void toEntity() {
      final RoleEntity entity = roleMapper.toEntity(this.role);
      assertNotNull(entity, "Should not be null.");
   }

   @Test
   @DisplayName("To Entity Set")
   void toEntitySet() {
      final Set<RoleEntity> roleEntitySet = roles.stream()
              .map(roleMapper::toEntity)
              .collect(Collectors.toSet());
      final Set<Role> roles = roleEntitySet.stream()
              .map(RoleEntity::getRole)
              .collect(Collectors.toSet());
      assertAll("Not null, not empty and contains both existing roles.",
              () -> assertNotNull(roleEntitySet, "Should not be null."),
              () -> assertFalse(roleEntitySet.isEmpty(), "Should not be empty."),
              () -> assertTrue(roles.containsAll(this.roles), "Should have the same content"));

   }
}
