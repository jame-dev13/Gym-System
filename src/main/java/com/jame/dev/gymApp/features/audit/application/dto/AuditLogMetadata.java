package com.jame.dev.gymApp.features.audit.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuditLogMetadata(
   @JsonProperty("ip") String ip,
   @JsonProperty("userAgent") String userAgent
) {
}
