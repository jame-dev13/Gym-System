package com.jame.dev.gymApp.features.audit.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;

public record AuditLogEntity(
   @JsonProperty("type") AuditLogEntityType type,
   @JsonProperty("entityId") long entityId
) {
}
