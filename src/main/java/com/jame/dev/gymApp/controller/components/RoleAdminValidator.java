package com.jame.dev.gymApp.controller.components;

import com.jame.dev.gymApp.controller.annotations.NoAdminValue;
import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;
import java.util.Set;

public class RoleAdminValidator implements ConstraintValidator<NoAdminValue, Set<Role>> {
   @Override
   public boolean isValid(Set<Role> roles, ConstraintValidatorContext context) {
      return Optional.ofNullable(roles)
              .orElse(Set.of())
              .stream()
              .noneMatch(Role.ADMIN::equals);
   }
}
