package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
public class AuditExecutionContextFactory {

   public AuditExecutionContext create(final ProceedingJoinPoint joinPoint) {
      final var signature = (MethodSignature) joinPoint.getSignature();
      final var method = signature.getMethod();
      final var annotation = method.getAnnotation(AuditLog.class);

      return AuditExecutionContext.builder()
         .args(joinPoint.getArgs())
         .paramNames(signature.getParameterNames())
         .annotation(annotation)
         .build();
   }

}
