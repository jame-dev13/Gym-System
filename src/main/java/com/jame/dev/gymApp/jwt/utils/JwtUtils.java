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
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Log
@Component
public class JwtUtils {

   @Value("${jwt.secret.key}")
   private String secret;

   public Key signWith() {
      try {
         final byte[] bytes = Decoders.BASE64.decode(secret);
         return Keys.hmacShaKeyFor(bytes);
      } catch (DecodingException | WeakKeyException e) {
         throw new InvalidSignedJwtKeyException("Invalid secret key.", e);
      }
   }

   public <T> Optional<T> getClaim(final String token, final Function<Claims, T> function) {
      try {
         Claims claims = Jwts.parserBuilder()
                 .setSigningKey(signWith())
                 .build()
                 .parseClaimsJws(token)
                 .getBody();
         return Optional.of(function.apply(claims));
      } catch (JwtException e) {
         log.severe("Cant´t parse the Claims: " + e.getMessage());
         return Optional.empty();
      }
   }

   public String buildToken(final String username, final long expiration){
      try {
         return Jwts.builder()
                 .signWith(signWith())
                 .setIssuedAt(new Date())
                 .setSubject(username)
                 .setExpiration(new Date(System.currentTimeMillis() + expiration))
                 .compact();
      } catch (InvalidKeyException e) {
         log.severe("Can´t build Jwt Token: " + e.getMessage());
         throw new InvalidSignedJwtKeyException("Signed key is not valid.");
      }
   }
}
