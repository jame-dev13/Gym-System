package com.jame.dev.gymApp.features.auth.domain.event;

public record UserNotifiableEvent(
        String email, String rawPassword
) {
}
