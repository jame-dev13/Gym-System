package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@Builder
public record UserDtoInput(
        @JsonProperty("name") @NotEmptyNull String name,
        @JsonProperty("email") @EmailValid String email,
        @JsonProperty("password") @Nullable String password,
        @JsonProperty("authProvider") @NotNullObject AuthProvider authProvider,
        @JsonProperty("roles") @NotNullObject Set<Role> roles
) {
}
