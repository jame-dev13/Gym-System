package com.jame.dev.gymApp.model.listeners;

public record RecoverySenderEvent(
        String email, String token
) {
}
