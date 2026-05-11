package com.jame.dev.gymApp.features.auth.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;

public record RecoveryAccountRequest(
        @JsonProperty("email")
        @EmailValid String email,
        @JsonProperty("token")
        @NotEmptyNull String token
) {
}
