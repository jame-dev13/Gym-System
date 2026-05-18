package com.jame.dev.gymApp.features.audit.infrastructure.annotation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditLog {
   AuditLogAction action() default AuditLogAction.UNKNOW;
   AuditLogEntityType entityType() default AuditLogEntityType.NO_SET;
   String input() default "";
   String entityId() default "";
   String result() default "";
}
