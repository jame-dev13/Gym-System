package com.jame.dev.gymApp.features.auth.application.model;

public record JwtValidationArgument(
   String token, String subject, long userId
) {
}
