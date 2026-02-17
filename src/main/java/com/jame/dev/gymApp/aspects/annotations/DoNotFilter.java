package com.jame.dev.gymApp.aspects.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface DoNotFilter {
   String filterName() default "deletedFilter";
}
