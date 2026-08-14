package com.jame.dev.gymApp.features.audit.infrastructure.execution;

import com.jame.dev.gymApp.features.audit.application.support.factory.AuditExecutionContextFactory;
import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogInputFactory;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_registry.AuditResolverRegistry;
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
   private final AuditResolverRegistry auditResolverRegistry;
   private final AuditLogEventPublisher eventPublisher;
   private final AuditLogInputFactory auditLogInputFactory;

   public Object coordinate(final ProceedingJoinPoint joinPoint) throws Throwable {
      final var context = executionContextFactory.create(joinPoint);
      if (context.getAnnotation() == null)
         return joinPoint.proceed();

      final var ACTION = context.getAnnotation().action();


      try {
         auditResolverRegistry.before(ACTION).resolve(context);

         final Object result = joinPoint.proceed();

         context.setResult(result);

         if (auditResolverRegistry.checkAfterRegistry(ACTION)) {
            auditResolverRegistry.after(ACTION).resolve(context);
         }

         return result;
      } catch (final Throwable th) {
         context.setTh(th);
         log.error("Exception during audit coordination.", th);
         throw th;
      } finally {
         try {
            final var input = auditLogInputFactory.create(context);
            eventPublisher.publishAuditLogEvent(input);
         } catch (final Exception ex) {
            log.error("Failed to publish audit event", ex);
         }
      }
   }
}
