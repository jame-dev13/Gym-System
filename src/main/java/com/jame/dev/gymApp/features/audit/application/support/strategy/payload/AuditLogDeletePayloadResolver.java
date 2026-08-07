package com.jame.dev.gymApp.features.audit.application.support.strategy.payload;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_registry.AuditLogCrudResolverRegistry;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.payload.AuditLogPayloadResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuditLogDeletePayloadResolver implements AuditLogPayloadResolver {
   private final AuditLogCrudResolverRegistry crudRegistry;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.DELETE;
   }

   @Override
   public AuditPayload resolve(AuditExecutionContext ctx) {
      return crudRegistry
         .check(ctx.getAnnotation().entityType())
         .resolve(ctx);
   }
}
