package com.jame.dev.gymApp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignUpDto (
        @JsonProperty("name")
        @NotEmptyNull
        String name,
        @JsonProperty("email")
        @EmailValid
        String email,
        @JsonProperty("password")
        @NotEmptyNull
        String password
) {
}
