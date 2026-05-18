package com.jame.dev.gymApp.features.audit.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuditLogChanges(
   @JsonProperty("before") Object before,
   @JsonProperty("after") Object after
) {
}
