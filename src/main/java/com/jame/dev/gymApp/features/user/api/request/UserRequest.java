package com.jame.dev.gymApp.features.user.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@Builder
public record UserRequest(
        @JsonProperty("name") @NotEmptyNull String name,
        @JsonProperty("email") @EmailValid String email,
        @JsonProperty("password") @Nullable String password,
        @JsonProperty("authProvider") @NotNullObject AuthProvider authProvider,
        @JsonProperty("roles")
        @NotNullObject
        @NotEmpty(message = "Roles shouldn't be empty")
        Set<Role> roles
) {
}
