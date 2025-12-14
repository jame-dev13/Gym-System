package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

@Builder
public record CustomerDtoInput(
        @JsonProperty("userId") @NonNull Long userId,
        @JsonProperty("contact") @Nullable String contact
) {

}
