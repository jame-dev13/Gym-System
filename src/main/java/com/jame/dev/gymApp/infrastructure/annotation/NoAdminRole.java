package com.jame.dev.gymApp.infrastructure.annotation;

import com.jame.dev.gymApp.infrastructure.constraint.NoAdminRoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD})
@Documented
@Constraint(validatedBy = NoAdminRoleValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoAdminRole {
   String message() default "Role value isn't allowed here.";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
