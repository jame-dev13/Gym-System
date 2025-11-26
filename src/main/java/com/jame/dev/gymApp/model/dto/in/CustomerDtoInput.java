package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record CustomerDtoInput(
        @JsonProperty("userId") @NonNull Long userId,
        @JsonProperty("active") @NonNull Boolean active
) {
   public CustomerDtoInput{
      active = Boolean.TRUE;
   }
}
