package com.jame.dev.gymApp.infrastructure.security.lock;


import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockProcess {
   @NotNull(message = "Process key shouldn't be null.") LockKeys processKey();
}
