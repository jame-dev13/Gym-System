package com.jame.dev.gymApp.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExtendExpirationRequest(
        @NotNull
        @NotBlank
        @Email String email
) {
}
