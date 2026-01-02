package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.controller.annotations.NoAdminValue;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@Builder
public record UserDtoInput(
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("email") @NotBlank @Email String email,
        @JsonProperty("password") @Nullable String password,
        @JsonProperty("authProvider") @NonNull AuthProvider authProvider,
        @JsonProperty("roles") @NotNull @NoAdminValue Set<Role> roles
) {
}
