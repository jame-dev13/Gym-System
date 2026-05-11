package com.jame.dev.gymApp.features.auth.domain.event;

public record PasswordResetEvent(
   String email, String rawToken
) {
}
