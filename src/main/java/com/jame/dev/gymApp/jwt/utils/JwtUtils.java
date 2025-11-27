package com.jame.dev.gymApp.jwt.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Log
public final class JwtUtils {

   @Value("${jwt.secret.key}")
   private static String secret;

   public static Key signWith() {
      final byte[] bytes = Decoders.BASE64.decode(secret);
      return Keys.hmacShaKeyFor(bytes);
   }

   public static <T> Optional<T> getClaim(final String token, final Function<Claims, T> function) {
      try {
         Claims claims = Jwts.parserBuilder()
                 .setSigningKey(signWith())
                 .build()
                 .parseClaimsJws(token)
                 .getBody();
         return Optional.of(function.apply(claims));
      } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException e) {
         log.severe("Cant´t parse the Claims: " + e.getMessage());
         return Optional.empty();
      }
   }

   public static String buildToken(final String username, final long expiration){
      return Jwts.builder()
              .signWith(signWith())
              .setIssuedAt(new Date())
              .setSubject(username)
              .setExpiration(new Date(System.currentTimeMillis() + expiration))
              .compact();
   }
}
