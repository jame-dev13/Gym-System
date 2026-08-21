package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuditLogEntityFactory {

   AuditLogEntity from(final AuditExecutionContext ctx) {
      return AuditLogEntity.builder()
         .type(Objects.requireNonNullElse(ctx.getAnnotation().entityType(), AuditLogEntityType.NO_SET))
         .entityId(Objects.requireNonNullElse(ctx.getEntityId(), -1L))
         .build();
   }

}
