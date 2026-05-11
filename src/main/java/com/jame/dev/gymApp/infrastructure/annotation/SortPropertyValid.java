package com.jame.dev.gymApp.infrastructure.annotation;


import com.jame.dev.gymApp.infrastructure.constraint.SortPropertyValidConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {SortPropertyValidConstraint.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface SortPropertyValid {
   String message() default "Sort property not allowed.";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
