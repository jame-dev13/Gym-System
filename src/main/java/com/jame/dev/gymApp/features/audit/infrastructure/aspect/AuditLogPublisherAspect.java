package com.jame.dev.gymApp.features.audit.infrastructure.aspect;

import com.jame.dev.gymApp.features.audit.infrastructure.execution.AuditLogCoordinator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(1)
public class AuditLogPublisherAspect {
   private final AuditLogCoordinator auditLogCoordinator;

   @Around(
      value = "@annotation(com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog)"
   )
   public Object publishAuditLog(final ProceedingJoinPoint joinPoint) throws Throwable {
      return auditLogCoordinator.coordinate(joinPoint);
   }
}
