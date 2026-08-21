package com.jame.dev.gymApp.features.audit.infrastructure.execution;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_registry.AuditResolverRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditResolverBlockExecutor {

   private final AuditResolverRegistry auditResolverRegistry;

   public void resolveBeforeState(
      final AuditLogAction action,
      final AuditExecutionContext context
   ) {
      if (!auditResolverRegistry.checkBeforeRegistry(action)) {
         log.debug("No before resolver registered for action {}. Skipping before-state resolution.", action);
         return;
      }
      try {
         auditResolverRegistry.before(action).resolve(context);
      } catch (Exception e) {
         log.warn("Before resolver failed for action {}. Continuing with service execution.", action, e);
      }
   }

   public void resolverAfterState(final AuditLogAction action, final AuditExecutionContext context) {
      if (!auditResolverRegistry.checkAfterRegistry(action))
         return;
      try {
         auditResolverRegistry.after(action).resolve(context);
      } catch (final Exception ex) {
         log.warn("After resolver failed for action {}. Returning service result anyway.", action, ex);
      }
   }

}
