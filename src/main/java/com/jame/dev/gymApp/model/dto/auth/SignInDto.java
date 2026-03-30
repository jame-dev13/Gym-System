package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;

public record SignInDto(
        @JsonProperty("email")
        @EmailValid
        String email,
        @JsonProperty("password")
        @NotEmptyNull
        String password
) {
}
