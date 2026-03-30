package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import org.jspecify.annotations.Nullable;

public record CustomerDtoInput(
        @JsonProperty("userEmail")
        @EmailValid String email,
        @JsonProperty("contact")
        @Nullable
        String contact
) {
}
