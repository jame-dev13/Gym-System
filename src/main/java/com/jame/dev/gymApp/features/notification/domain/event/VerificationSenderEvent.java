package com.jame.dev.gymApp.features.notification.domain.event;

public record VerificationSenderEvent(
   String email, String token
) {
}
