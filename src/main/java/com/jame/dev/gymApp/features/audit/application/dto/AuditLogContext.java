package com.jame.dev.gymApp.features.audit.application.dto;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;

public record AuditLogContext(
   AuditLogAction action,
   AuditLogEntityType entityType) {
}
