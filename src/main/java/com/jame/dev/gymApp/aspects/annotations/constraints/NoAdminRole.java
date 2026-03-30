package com.jame.dev.gymApp.aspects.annotations.constraints;

import com.jame.dev.gymApp.aspects.imp.constraints.NoAdminRoleValidator;
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
