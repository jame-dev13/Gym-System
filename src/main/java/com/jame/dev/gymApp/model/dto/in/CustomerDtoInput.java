package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record CustomerDtoInput(
        @JsonProperty("userId") @NonNull Long userId,
        @JsonProperty("contact") @NonNull @NotBlank String contact
) {

}
