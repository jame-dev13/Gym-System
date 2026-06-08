package com.jame.dev.gymApp.features.auth.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;

public record RegisterRequest(
   @NotEmptyNull
   @JsonProperty("name")
   String name,
   @NotNullObject
   @EmailValid
   @JsonProperty("email")
   String email,
   @NotEmptyNull
   @JsonProperty("password")
   String password
) {
}
