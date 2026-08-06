package com.jame.dev.gymApp.features.audit.domain.model;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogChanges;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import lombok.Builder;

@Builder
public record AuditLogInput(
   AuditLogEntity entity,
   AuditLogAction auditLogAction,
   AuditLogActor actor,
   AuditLogChanges changes,
   boolean success,
   AuditLogMetadata metadata
) {
}
