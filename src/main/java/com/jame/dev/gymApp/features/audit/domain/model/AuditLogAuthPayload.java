package com.jame.dev.gymApp.features.audit.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record AuditLogAuthPayload(
   @JsonProperty("info") Map<String, Object> info
) implements AuditPayload {
   @Override
   public AuditPayloadType type() {
      return AuditPayloadType.AUTHENTICATION;
   }
}
