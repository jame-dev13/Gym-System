package com.jame.dev.gymApp.features.auth.api.request;

public record TokenIdResetPasswordRequest(
   String rawToken, long uid
) {
}
