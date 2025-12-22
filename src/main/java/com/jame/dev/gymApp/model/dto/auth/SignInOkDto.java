package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SignInOkDto(
        @JsonProperty("isCustomer") boolean isCustomer,
        @JsonProperty("msg") String msg,
        @JsonProperty("email") String email,
        @JsonIgnore String access,
        @JsonIgnore String refresh
) {
}
