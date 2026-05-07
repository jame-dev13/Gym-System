package com.jame.dev.gymApp.model.dto.auth;

public record TokenIdResetPasswordRequest(
   String rawToken, long uid
) {
}
