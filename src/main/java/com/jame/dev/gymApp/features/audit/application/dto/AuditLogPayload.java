package com.jame.dev.gymApp.features.audit.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.audit.domain.model.AuditPayload;

public record AuditLogPayload(
   @JsonProperty("payload") AuditPayload payload
) {
}
