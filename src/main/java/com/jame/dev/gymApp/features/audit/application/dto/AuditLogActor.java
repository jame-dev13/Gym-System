package com.jame.dev.gymApp.features.audit.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuditLogActor(
   @JsonProperty("userId") Long userId,
   @JsonProperty("username") String username
) {
}
