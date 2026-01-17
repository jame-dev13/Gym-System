package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ExpirationWindowDto(
        @JsonProperty("requestedAt")
        @NotNull
        OffsetDateTime requestAt,
        @JsonProperty("requestedBy")
        @NotNull
        @NotBlank
        @Email
        String email,
        @JsonProperty("updated")
        boolean updated,
        @JsonProperty("state")
        @NotBlank
        String state,
        @JsonProperty("expiresAt")
        @NotNull
        OffsetDateTime expiresAt,
        @JsonProperty("msg")
        String msg
) {
}
