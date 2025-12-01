package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CustomerDtoOutput(
        @JsonProperty("id") Long id,
        @JsonProperty("user") UserDtoOutput user,
        @JsonProperty("contact") String contact
) {
}
