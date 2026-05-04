package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserMinimalInfo(
   @JsonProperty("id") long id,
   @JsonProperty("name") String name,
   @JsonProperty("email") String email
) {
}
