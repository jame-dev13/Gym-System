package com.jame.dev.gymApp.features.auth.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SignInResponse(
        @JsonProperty("isCustomer") boolean isCustomer,
        @JsonProperty("isUser") boolean isUser,
        @JsonProperty("msg") String msg,
        @JsonProperty("email") String email,
        @JsonIgnore String access,
        @JsonIgnore String refresh
) {
}
