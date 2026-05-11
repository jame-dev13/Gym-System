package com.jame.dev.gymApp.infrastructure.constraint;

import com.jame.dev.gymApp.infrastructure.annotation.NoAdminRole;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoAdminRoleValidator implements ConstraintValidator<NoAdminRole, UserRequest> {

   @Override
   public void initialize(NoAdminRole constraintAnnotation) {
      ConstraintValidator.super.initialize(constraintAnnotation);
   }

   @Override
   public boolean isValid(UserRequest value, ConstraintValidatorContext context) {
      if (value == null || value.roles() == null) return true;

      return !value.roles().contains(Role.ADMIN);
   }
}
