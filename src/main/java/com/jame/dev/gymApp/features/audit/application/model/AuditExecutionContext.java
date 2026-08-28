package com.jame.dev.gymApp.features.audit.application.model;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogKind;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Builder
@Getter
@Setter
public class AuditExecutionContext {

   private AuditLog annotation;

   private String[] paramNames;

   private Object[] args;

   private Method method;

   private Object result;

   private Object resultValue;

   private Long entityId;

   private Object input;

   private Throwable th;

   private AuditLogActor auditLogActor;

   private AuditLogKind kind;
}
