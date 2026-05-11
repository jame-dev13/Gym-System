package com.jame.dev.gymApp.features.auth.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;

public record PasswordResetRequest(
   @JsonProperty("email") @EmailValid String email,
   @JsonProperty("newPassword") @NotEmptyNull String newPassword
) {
}
