package com.jame.dev.gymApp.jwt.utils;

import com.jame.dev.gymApp.exception.InvalidSignedJwtKeyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

   private final Clock clock;
   @Value("${jwt.secret.key}")
   private String secret;

   public SecretKey signWith() {
      try {
         final byte[] bytes = Decoders.BASE64.decode(secret);
         return Keys.hmacShaKeyFor(bytes);
      } catch (DecodingException | WeakKeyException e) {
         throw new InvalidSignedJwtKeyException("Invalid secret key.", e);
      }
   }

   public <T> Optional<T> getClaim(final String token, final Function<Claims, T> function) {
      try {
         final Claims claims = Jwts.parser()
                 .verifyWith(signWith())
                 .clock(() -> Date.from(clock.instant()))
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();
         return Optional.of(function.apply(claims));
      } catch (JwtException e) {
         log.error("Cant´t parse the Claims: {}", e.getMessage());
         return Optional.empty();
      }
   }

   public String buildToken(final String username, final long expiration) {
      try {
         return Jwts.builder()
                 .signWith(signWith())
                 .issuedAt(Date.from(Instant.now(clock)))
                 .subject(username)
                 .expiration(Date.from(Instant.now(clock).plus(expiration, ChronoUnit.MILLIS)))
                 .compact();
      } catch (InvalidKeyException e) {
         log.error("Can´t build Jwt Token: {}", e.getMessage());
         throw new InvalidSignedJwtKeyException("Signed key is not valid.", e);
      }
   }

   public Clock getClock(){
      return this.clock;
   }
}
