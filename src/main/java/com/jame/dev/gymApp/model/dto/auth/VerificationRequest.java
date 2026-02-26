package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;

public record VerificationRequest(
        @JsonProperty("token")
        @NotEmptyNull
        String token) {
}
