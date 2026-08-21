package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogActorHelper;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogMetadataHelper;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogErrorPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditLogInputFactory {

   private final ExtractAuditLogActorHelper extractAuditLogActorHelper;
   private final AuditLogEntityFactory entityFactory;
   private final AuditLogPayloadFactory auditPayloadFactory;

   public AuditLogInput create(AuditExecutionContext context) {
      final var ACTION = context.getAnnotation().action();
      final AuditLogEntity entity = entityFactory.from(context);
      final AuditLogActor actor = extractAuditLogActorHelper.extractLogActor(context);
      final AuditPayload payload = auditPayloadFactory.from(context);
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

   @Deprecated
   public AuditLogInput createFailureFallback(AuditExecutionContext context, Throwable cause) {
      final var annotation = context.getAnnotation();
      final AuditLogEntity entity = entityFactory.from(context);
      final AuditLogActor actor = extractAuditLogActorHelper.extractLogActor(context);
      final AuditPayload payload = AuditLogErrorPayload.builder()
         .error(Map.of(
            "reason", cause != null ? cause.getClass().getSimpleName() : "UNKNOWN",
            "description", cause != null ? cause.getMessage() : "Audit payload could not be resolved."))
         .build();
      final AuditLogMetadata metadata = ExtractAuditLogMetadataHelper.extractAuditLogMetadata();
      return AuditLogInput.builder()
         .entity(entity)
         .auditLogAction(annotation.action())
         .actor(actor)
         .payload(payload)
         .success(context.getTh() == null)
         .metadata(metadata)
         .build();
   }

}
