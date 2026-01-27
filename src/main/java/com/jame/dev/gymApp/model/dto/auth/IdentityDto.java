package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Role;

import java.util.Set;

public record IdentityDto(
        @JsonProperty("email")
        String email,
        @JsonProperty("roles")
        Set<Role> roles,
        @JsonProperty("isCustomer")
        boolean isCustomer
) {
}