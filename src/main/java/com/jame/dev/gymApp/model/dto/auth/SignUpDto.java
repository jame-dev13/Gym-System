package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignUpDto (
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("email") @Email String email,
        @JsonProperty("password") @NotBlank String password
) {
}
