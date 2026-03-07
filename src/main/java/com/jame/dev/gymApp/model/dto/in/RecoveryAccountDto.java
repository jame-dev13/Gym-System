package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;

public record RecoveryAccountDto(
        @JsonProperty("email")
        @EmailValid String email,
        @JsonProperty("token")
        @NotEmptyNull String token
) {
}
