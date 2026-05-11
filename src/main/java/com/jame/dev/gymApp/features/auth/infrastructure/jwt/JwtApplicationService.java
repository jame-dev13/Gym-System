package com.jame.dev.gymApp.features.auth.infrastructure.jwt;

import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import com.jame.dev.gymApp.features.auth.domain.exception.ExtractClaimException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtApplicationService implements JwtService {

   private final JwtUtils jwtUtils;

   @Value("${jwt.secret.expiration}")
   private Long expiration;
   @Value("${jwt.refresh.expiration}")
   private Long refreshExpiration;

   private static final String EXCEPTION_MESSAGE = "'Can't extract claim, perhaps Jwt Token's expired.'";

   @Override
   public String generateAccessToken(String username) {
      return jwtUtils.buildToken(username, expiration);
   }

   @Override
   public String generateRefreshToken(String username) {
      return jwtUtils.buildToken(username, refreshExpiration);
   }

   @Override
   public boolean isValid(String token, String subject) {
      final String subjectExtracted = extractSubject(token)
              .orElseThrow(() -> new ExtractClaimException(EXCEPTION_MESSAGE));
      final boolean isTokenExpired = isExpired(token);
      return !isTokenExpired && subject.equals(subjectExtracted);
   }

   @Override
   public boolean isExpired(String token) {
      final Clock clock = jwtUtils.getClock();
      final Instant expiration = extractExpiration(token)
              .orElseThrow(() -> new ExtractClaimException(EXCEPTION_MESSAGE))
              .toInstant();
      return Instant.now(clock).isAfter(expiration);
   }

   @Override
   public Optional<String> extractSubject(String token) {
      return jwtUtils.getClaim(token, Claims::getSubject);
   }

   @Override
   public Optional<Date> extractExpiration(String token) {
      return jwtUtils.getClaim(token, Claims::getExpiration);
   }

   @Override
   public Optional<String> extractJti(String token) {
      return jwtUtils.getClaim(token, Claims::getId);
   }
}
