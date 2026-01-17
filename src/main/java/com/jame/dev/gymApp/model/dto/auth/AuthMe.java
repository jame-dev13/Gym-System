package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AuthMe(
        @JsonProperty("email")
        @NotNull
        @NotBlank
        @Email
        String email,
        @JsonProperty("roles")
        Set<Role> roles
) {
}
