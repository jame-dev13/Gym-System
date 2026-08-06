package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogChanges;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogActorHelper;
import com.jame.dev.gymApp.features.audit.application.support.helper.ExtractAuditLogMetadataHelper;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogInputFactory {

   public AuditLogInput create(AuditExecutionContext context) {
      final var ACTION = context.getAnnotation().action();
      final AuditLogEntity entity = new AuditLogEntity(context
         .getAnnotation().entityType(), context.getEntityId());
      final AuditLogActor actor = ExtractAuditLogActorHelper.extractLogActor();
      final AuditLogChanges changes = AuditLogChangesFactory.createAuditLogChangesFrom(ACTION, context.getEntityId(), context.getInput(), context.getResultValue());
      final AuditLogMetadata metadata = ExtractAuditLogMetadataHelper.extractAuditLogMetadata();
      return AuditLogInput.builder()
         .entity(entity)
         .auditLogAction(ACTION)
         .actor(actor)
         .changes(changes)
         .success(context.getTh() == null)
         .metadata(metadata)
         .build();
   }

}
