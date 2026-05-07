package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotEmptyNull;

public record PasswordResetDtoInput(
   @JsonProperty("email") @EmailValid String email,
   @JsonProperty("newPassword") @NotEmptyNull String newPassword
) {
}
