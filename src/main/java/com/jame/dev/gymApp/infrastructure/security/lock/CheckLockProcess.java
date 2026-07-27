package com.jame.dev.gymApp.infrastructure.security.lock;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckLockProcess {
   LockKeys[] keys() default { LockKeys.PG_DUMP, LockKeys.PG_RESTORE };
}
