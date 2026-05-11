package com.jame.dev.gymApp.infrastructure.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Target({ElementType.FIELD,  ElementType.PARAMETER, ElementType.TYPE})
@Documented
@Inherited
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = "Filed is null.")
@NotBlank(message = "Filed is in blank.")
public @interface NotEmptyNull {
   String message() default "Field must not be empty/blank or null.";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
}
