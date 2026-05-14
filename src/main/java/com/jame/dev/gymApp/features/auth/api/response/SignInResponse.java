package com.jame.dev.gymApp.features.auth.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SignInResponse(
        @JsonProperty("isUser") boolean isUser,
        @JsonProperty("email") String email,
        @JsonIgnore String access,
        @JsonIgnore String refresh
) {
}
