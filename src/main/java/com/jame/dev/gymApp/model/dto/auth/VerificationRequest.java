package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;

public record VerificationRequest(
        @JsonProperty("token")
        @NotEmptyNull
        String token) {
}
