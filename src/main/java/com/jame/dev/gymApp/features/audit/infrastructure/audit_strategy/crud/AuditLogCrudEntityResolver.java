package com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.crud;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogCrudPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;

import java.util.Map;

public interface AuditLogCrudEntityResolver {

   AuditLogEntityType entity();

   AuditLogCrudPayload resolveUpdate(final AuditExecutionContext ctx);

   default AuditLogCrudPayload resolve(final AuditExecutionContext ctx) {
      final var annotation = ctx.getAnnotation();
      return switch (annotation.action()) {
         case INSERT -> AuditLogCrudPayload.builder()
            .before(Map.of("status", "UNEXISTING"))
            .after(Map.of("status", "CREATED"))
            .build();
         case RECOVER -> AuditLogCrudPayload.builder()
            .before(Map.of("active", false))
            .after(Map.of(
               "active", true,
               "entityId", ctx.getEntityId(),
               "entityType", annotation.entityType()))
            .build();
         case DELETE -> AuditLogCrudPayload.builder()
            .before(Map.of("active", true))
            .after(Map.of("active", false))
            .build();
         case HARD_DELETE -> AuditLogCrudPayload.builder()
            .before(Map.of(
               "entityType", annotation.entityType(),
               "entityId", ctx.getEntityId()))
            .after(Map.of("status", "Permanently deleted."))
            .build();
         default -> throw new IllegalArgumentException("Cannot handling the action: " + annotation.action());
      };
   }
}
