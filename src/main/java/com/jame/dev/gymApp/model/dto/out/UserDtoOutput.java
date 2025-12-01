package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.Builder;

@Builder
public record UserDtoOutput(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("role") Role role
) {
}
