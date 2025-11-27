package com.jame.dev.gymApp.jwt.service;

import java.util.Date;
import java.util.Optional;

public interface JwtService {
   String generateAccessToken(final String username);
   String generateRefreshToken(final String username);
   boolean isValid(final String token, final String subject);
   boolean isExpired(final String token);
   Optional<String> extractSubject(final String token);
   Optional<Date> extractExpiration(final String token);
}
