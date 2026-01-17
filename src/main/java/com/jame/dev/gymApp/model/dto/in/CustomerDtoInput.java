package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

public record CustomerDtoInput(
        @JsonProperty("userEmail") @NotNull @NotBlank @Email String email,
        @JsonProperty("contact") @Nullable String contact
) {
}
