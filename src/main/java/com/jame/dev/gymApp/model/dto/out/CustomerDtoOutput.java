package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.NonNull;

@Builder
@JsonSerialize
public record CustomerDtoOutput(
        @JsonProperty("id") @NonNull Long id,
        @JsonProperty("user") @NonNull UserDtoOutput user
) {
}
