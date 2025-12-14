package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record VerificationDto(
        @JsonProperty("timestamp") OffsetDateTime timestamp,
        @JsonProperty("email") String email,
        @JsonProperty("isVerified") boolean verified,
        @JsonProperty("msg") String msg
) {
}
