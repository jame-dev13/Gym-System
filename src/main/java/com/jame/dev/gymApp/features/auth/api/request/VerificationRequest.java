package com.jame.dev.gymApp.features.auth.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;

public record VerificationRequest(
        @JsonProperty("token")
        @NotEmptyNull
        String token) {
}
