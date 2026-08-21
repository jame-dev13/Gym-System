package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogErrorPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_registry.AuditLogPayloadResolverRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogPayloadFactory {

   private final AuditLogPayloadResolverRegistry resolverRegistry;

   public final AuditPayload from(AuditExecutionContext ctx) {
      final var ACTION = ctx.getAnnotation().action();
      if (ctx.getTh() != null || !resolverRegistry.exists(ACTION)) {
         final String simpleName = Objects
            .requireNonNullElse(ctx.getTh().getClass().getSimpleName(), "UNKNOWN");
         final String description = Objects
            .requireNonNullElse(ctx.getTh().getMessage(), "Audit payload could not be resolved.");
         return AuditLogErrorPayload.builder()
            .error(Map.of(
               "reason", simpleName,
               "description", description))
            .build();
      }

      return resolverRegistry.get(ACTION).resolve(ctx);

   }
}
