package com.jame.dev.gymApp.cache.service;

import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.lang.Math.abs;

@Service
@RequiredArgsConstructor
public class TokenServiceImplementation implements BlacklistService {

   private final StringRedisTemplate tokensRedisTemplate;
   private final JwtService jwtService;

   @Override
   public void blacklistToken(String key) {
      if (tokensRedisTemplate.hasKey(key)) {
         throw new TokenAlreadyBlacklistedException("Token already blacklisted.");
      }

      final Instant expiration = jwtService.extractExpiration(key)
              .orElseThrow(() -> new ExtractClaimException("Can't extract claims."))
              .toInstant();

      final long ttl = abs(
              Duration.between(Instant.now(), expiration).getSeconds()
      );
      if (ttl <= 0) return;
      tokensRedisTemplate.opsForValue().set(key, "blacklisted", Duration.of(ttl, ChronoUnit.SECONDS));
   }

   @Override
   public boolean isBlacklisted(String key) {
      return tokensRedisTemplate.hasKey(key);
   }
}
