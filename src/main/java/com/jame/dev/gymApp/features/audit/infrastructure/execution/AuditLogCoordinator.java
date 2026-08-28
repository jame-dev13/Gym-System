package com.jame.dev.gymApp.features.audit.infrastructure.execution;

import com.jame.dev.gymApp.features.audit.application.support.factory.AuditExecutionContextFactory;
import com.jame.dev.gymApp.features.audit.infrastructure.publisher.AuditLogEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogCoordinator {

   private final AuditExecutionContextFactory executionContextFactory;
   private final AuditLogEventPublisher eventPublisher;
   private final AuditResolverBlockExecutor blockExecutor;

   public Object coordinate(final ProceedingJoinPoint joinPoint) throws Throwable {
      final var context = executionContextFactory.create(joinPoint);
      if (context.getAnnotation() == null)
         return joinPoint.proceed();

      final var action = context.getAnnotation().action();

      blockExecutor.resolveBeforeState(action, context);

      try {
         final Object result = joinPoint.proceed();
         context.setResult(result);
         blockExecutor.resolverAfterState(action, context);
         blockExecutor.resolveBindingAction(action, context);
         return result;
      } catch (final Throwable th) {
         context.setTh(th);
         log.error("Exception during audited method execution.", th);
         throw th;
      } finally {
         eventPublisher.publishAuditLogEventSafely(context);
      }
   }
}
