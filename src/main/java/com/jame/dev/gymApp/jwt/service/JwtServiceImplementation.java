package com.jame.dev.gymApp.jwt.service;

import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.jwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtServiceImplementation implements JwtService {

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
      final Date expiration = extractExpiration(token)
              .orElseThrow(() -> new ExtractClaimException(EXCEPTION_MESSAGE));
      final Date currentTime = new Date(System.currentTimeMillis());
      return currentTime.after(expiration);
   }

   @Override
   public Optional<String> extractSubject(String token) {
      return jwtUtils.getClaim(token, Claims::getSubject);
   }

   @Override
   public Optional<Date> extractExpiration(String token) {
      return jwtUtils.getClaim(token, Claims::getExpiration);
   }
}
