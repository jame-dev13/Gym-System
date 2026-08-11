package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogActorHelper;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogMetadataHelper;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_registry.AuditLogPayloadResolverRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogInputFactory {

   private final AuditLogPayloadResolverRegistry resolverRegistry;

   public AuditLogInput create(AuditExecutionContext context) {
      final var ACTION = context.getAnnotation().action();
      final var TYPE = context.getAnnotation().entityType();
      final AuditLogEntity entity = AuditLogEntity.builder()
         .entityId(context.getEntityId())
         .type(TYPE)
         .build();
      final AuditLogActor actor = ExtractAuditLogActorHelper.extractLogActor(context);
      final AuditPayload payload = resolverRegistry.check(ACTION).resolve(context);
      final AuditLogMetadata metadata = ExtractAuditLogMetadataHelper.extractAuditLogMetadata();
      return AuditLogInput.builder()
         .entity(entity)
         .auditLogAction(ACTION)
         .actor(actor)
         .payload(payload)
         .success(context.getTh() == null)
         .metadata(metadata)
         .build();
   }

}
