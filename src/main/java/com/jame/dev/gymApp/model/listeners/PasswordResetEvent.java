package com.jame.dev.gymApp.model.listeners;

public record PasswordResetEvent(
   String email, String rawToken
) {
}
