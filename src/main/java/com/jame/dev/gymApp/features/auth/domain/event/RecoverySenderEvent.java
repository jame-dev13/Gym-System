package com.jame.dev.gymApp.features.auth.domain.event;

public record RecoverySenderEvent(
        String email, String token
) {
}
