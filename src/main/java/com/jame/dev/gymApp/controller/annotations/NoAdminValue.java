package com.jame.dev.gymApp.controller.annotations;

import com.jame.dev.gymApp.controller.components.RoleAdminValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleAdminValidator.class)
@Target( {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoAdminValue {
   String message() default "Admin role is not allowed here";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
}
