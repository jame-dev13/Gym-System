package com.jame.dev.gymApp.features.audit.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import lombok.Builder;

@Builder
public record AuditLogEntity(
   @JsonProperty("type") AuditLogEntityType type,
   @JsonProperty("entityId") Long entityId
) {
}
