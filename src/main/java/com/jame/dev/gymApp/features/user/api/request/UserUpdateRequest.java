package com.jame.dev.gymApp.features.user.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserUpdateRequest(
   @JsonProperty("name")
   @NotEmpty(message = "Name shouldn't be empty") String name,
   @JsonProperty("email") @EmailValid String email,
   @JsonProperty("roles")
   @NotEmpty(message = "Roles shouldn't be empty")
   Set<Role> roles
) {
}
