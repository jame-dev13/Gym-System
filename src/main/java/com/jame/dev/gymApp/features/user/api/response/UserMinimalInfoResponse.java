package com.jame.dev.gymApp.features.user.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserMinimalInfoResponse(
   @JsonProperty("id") long id,
   @JsonProperty("name") String name,
   @JsonProperty("email") String email
) {
}
