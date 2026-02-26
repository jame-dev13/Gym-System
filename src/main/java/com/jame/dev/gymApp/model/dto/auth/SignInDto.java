package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;

public record SignInDto(
        @JsonProperty("email")
        @EmailValid
        String email,
        @JsonProperty("password")
        @NotEmptyNull
        String password
) {
}
