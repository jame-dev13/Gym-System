package com.jame.dev.gymApp.aspects.annotations.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Min;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE,
        ElementType.PARAMETER, ElementType.TYPE_PARAMETER,
        ElementType.CONSTRUCTOR, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Constraint(validatedBy = {})
@Min(1)
public @interface Minimum {
   @OverridesAttribute(constraint = Min.class, name = "value")
   long value() default 1;

   @OverridesAttribute(constraint = Min.class, name = "message")
   String message() default "Value must be positive integer and non cero.";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
