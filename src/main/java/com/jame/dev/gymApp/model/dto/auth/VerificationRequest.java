package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record VerificationRequest(@JsonProperty("token") @NotBlank String token) {
}
