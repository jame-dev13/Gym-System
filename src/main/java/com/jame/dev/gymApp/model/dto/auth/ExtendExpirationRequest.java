package com.jame.dev.gymApp.model.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ExtendExpirationRequest(
        @NotBlank String email
) {
}
