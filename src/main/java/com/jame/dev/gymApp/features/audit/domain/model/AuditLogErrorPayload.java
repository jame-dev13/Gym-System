package com.jame.dev.gymApp.features.audit.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record AuditLogErrorPayload(
   @JsonProperty("error") Map<String, Object> error
   ) implements AuditPayload {
   @Override
   @JsonProperty("type")
   public AuditPayloadType type() {
      return AuditPayloadType.ERROR;
   }
}