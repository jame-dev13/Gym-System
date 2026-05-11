package com.jame.dev.gymApp.features.auth.domain.model;

import com.jame.dev.gymApp.features.user.domain.model.Role;
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
