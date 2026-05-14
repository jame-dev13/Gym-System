package com.jame.dev.gymApp.features.auth.application.contract;

import com.jame.dev.gymApp.features.auth.application.model.JwtValidationArgument;

import java.util.Date;
import java.util.Optional;

public interface JwtService {
   String generateAccessToken(long userId, final String username);
   String generateRefreshToken(long userId, final String username);
   boolean isValid(final JwtValidationArgument jwtValidationArgument);
   boolean isExpired(final String token);
   Optional<String> extractSubject(final String token);
   Optional<Date> extractExpiration(final String token);
   Optional<String> extractJti(final  String token);
   Optional<Long> extractUserId(final String token);
}
