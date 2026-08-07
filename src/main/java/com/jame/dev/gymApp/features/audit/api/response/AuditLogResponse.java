package com.jame.dev.gymApp.features.audit.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.audit.application.dto.*;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;

import java.time.Instant;

public record AuditLogResponse(
   @JsonProperty("entity") AuditLogEntity entity,
   @JsonProperty("action") AuditLogAction action,
   @JsonProperty("actor") AuditLogActor actor,
   @JsonProperty("payload") AuditPayload payload,
   @JsonProperty("success") boolean success,
   @JsonProperty("metadata") AuditLogMetadata metadata,
   @JsonProperty("createdAt") Instant createdAt
) {
}
