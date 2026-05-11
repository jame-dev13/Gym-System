package com.jame.dev.gymApp.features.user.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("role") Set<Role> roles
) {
}
