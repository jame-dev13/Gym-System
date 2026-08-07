package com.jame.dev.gymApp.features.audit.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record AuditLogCrudPayload(
   @JsonProperty("before") Map<String, Object> before,
   @JsonProperty("after") Map<String, Object> after
) implements AuditPayload {
   @Override
   @JsonProperty("type")
   public AuditPayloadType type() {
      return AuditPayloadType.CRUD;
   }
}
