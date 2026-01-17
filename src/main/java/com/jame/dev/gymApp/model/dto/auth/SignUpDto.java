package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignUpDto (
        @JsonProperty("name")
        @NotNull
        @NotBlank
        String name,
        @JsonProperty("email")
        @NotNull
        @NotBlank
        @Email
        String email,
        @JsonProperty("password")
        @NotBlank
        @NotNull
        String password
) {
}
