package com.jame.dev.gymApp.aspects.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.PositiveOrZero;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Constraint(validatedBy = {})
@PositiveOrZero(message = "Value cannot be 0.")
public @interface PositiveNum {
   String message() default "Value must be positive integer or cero.";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
}
