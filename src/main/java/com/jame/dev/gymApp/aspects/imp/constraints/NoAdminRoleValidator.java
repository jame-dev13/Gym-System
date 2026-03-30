package com.jame.dev.gymApp.aspects.imp.constraints;

import com.jame.dev.gymApp.aspects.annotations.constraints.NoAdminRole;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoAdminRoleValidator implements ConstraintValidator<NoAdminRole, UserDtoInput> {

   @Override
   public void initialize(NoAdminRole constraintAnnotation) {
      ConstraintValidator.super.initialize(constraintAnnotation);
   }

   @Override
   public boolean isValid(UserDtoInput value, ConstraintValidatorContext context) {
      if (value == null || value.roles() == null) return true;

      return !value.roles().contains(Role.ADMIN);
   }
}
