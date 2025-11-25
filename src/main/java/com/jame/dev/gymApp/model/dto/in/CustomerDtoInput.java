package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.NonNull;

@Builder
@JsonDeserialize
public record CustomerDtoInput(
        @JsonProperty("userId") @NonNull Long userId,
        @JsonProperty("active") @NonNull Boolean active
) {
   public CustomerDtoInput{
      active = Boolean.TRUE;
   }
}
