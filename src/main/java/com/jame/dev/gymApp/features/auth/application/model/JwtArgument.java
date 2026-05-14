package com.jame.dev.gymApp.features.auth.application.model;

public record JwtArgument(
   long userId, String subject, long expiration
) {
}
