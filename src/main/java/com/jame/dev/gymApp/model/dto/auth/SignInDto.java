package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignInDto(
        @JsonProperty("email") @Email String email,
        @JsonProperty("password") @NotBlank String password
) {
}
