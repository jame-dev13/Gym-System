package com.jame.dev.gymApp.features.auth.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.user.domain.model.Role;

import java.util.Set;

public record SessionResponse(
        @JsonProperty("email")
        String email,
        @JsonProperty("roles")
        Set<Role> roles,
        @JsonProperty("isCustomer")
        boolean isCustomer
) {
}