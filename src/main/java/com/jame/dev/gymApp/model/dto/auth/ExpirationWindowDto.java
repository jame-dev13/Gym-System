package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ExpirationWindowDto(
        @JsonProperty("requestedAt") OffsetDateTime requestAt,
        @JsonProperty("requestedBy") String email,
        @JsonProperty("updated") boolean updated,
        @JsonProperty("state") String state,
        @JsonProperty("expiresAt") OffsetDateTime expiresAt,
        @JsonProperty("msg") String msg
) {
}
