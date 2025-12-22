package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record CustomerDtoInput(
        @JsonProperty("userEmail") @NotBlank String email,
        @JsonProperty("contact") @Nullable String contact
) {
}
