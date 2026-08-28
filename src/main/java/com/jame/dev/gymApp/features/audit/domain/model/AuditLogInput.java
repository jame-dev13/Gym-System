package com.jame.dev.gymApp.features.audit.domain.model;

import com.jame.dev.gymApp.features.audit.application.dto.*;
import lombok.Builder;

@Builder
public record AuditLogInput(
   AuditLogEntity entity,
   AuditLogAction auditLogAction,
   AuditLogActor actor,
   AuditPayload payload,
   AuditLogKind auditLogKind,
   boolean success,
   AuditLogMetadata metadata
) {
}
