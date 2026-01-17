package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignInDto(
        @JsonProperty("email")
        @NotNull
        @NotBlank
        @Email
        String email,
        @JsonProperty("password")
        @NotNull
        @NotBlank
        String password
) {
}
