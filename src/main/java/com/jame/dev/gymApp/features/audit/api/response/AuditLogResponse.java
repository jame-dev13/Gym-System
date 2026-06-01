package com.jame.dev.gymApp.features.audit.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogChanges;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;

import java.time.Instant;

public record AuditLogResponse(
   @JsonProperty("entity") AuditLogEntity entity,
   @JsonProperty("action") AuditLogAction action,
   @JsonProperty("actor") AuditLogActor actor,
   @JsonProperty("changes") AuditLogChanges changes,
   @JsonProperty("metadata") AuditLogMetadata metadata,
   @JsonProperty("createdAt") Instant createdAt
) {
}
