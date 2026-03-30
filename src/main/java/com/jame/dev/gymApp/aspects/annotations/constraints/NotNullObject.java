package com.jame.dev.gymApp.aspects.annotations.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.FIELD})
@Documented
@Inherited
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = "Object should not be null.")
public @interface NotNullObject {
   String message() default "Object must not be null.";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
}
