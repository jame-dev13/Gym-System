package com.jame.dev.gymApp.oauth2.model;

import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record AuthenticatedUser(
        @NotNull Long id,
        @NotBlank String name,
        @NotNull @NotBlank String email,
        @NotNull Set<Role> roles
) {
}
