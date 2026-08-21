package com.jame.dev.gymApp.features.audit.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ErrorPayload(
   @JsonProperty("reason") String reason,
   @JsonProperty("description") String description
) {
}
