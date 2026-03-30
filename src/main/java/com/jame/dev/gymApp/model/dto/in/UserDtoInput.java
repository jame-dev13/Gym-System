package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@Builder
public record UserDtoInput(
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
