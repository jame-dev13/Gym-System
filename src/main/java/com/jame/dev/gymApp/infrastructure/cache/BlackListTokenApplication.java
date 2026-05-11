package com.jame.dev.gymApp.infrastructure.cache;

import com.jame.dev.gymApp.features.auth.domain.exception.ExtractClaimException;
import com.jame.dev.gymApp.features.auth.domain.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.lang.Boolean.TRUE;
import static java.lang.Math.abs;

@Service
@RequiredArgsConstructor
public class BlackListTokenApplication implements BlacklistService {

   private final StringRedisTemplate tokensRedisTemplate;
   private final JwtService jwtService;

   @Override
   public void blacklistToken(String key) {
      final String jti = jwtService.extractJti(key)
              .orElseThrow(() -> new ExtractClaimException("JTI not found in token."));

      if (TRUE.equals(tokensRedisTemplate.hasKey(jti))) {
         throw new TokenAlreadyBlacklistedException("Token already blacklisted.");
      }

      final Instant expiration = jwtService.extractExpiration(key)
              .orElseThrow(() -> new ExtractClaimException("Can't extract claims."))
              .toInstant();

      final long ttl = abs(
              Duration.between(Instant.now(), expiration).getSeconds()
      );
      if (ttl <= 0) return;
      tokensRedisTemplate.opsForValue().set(jti, "blacklisted", Duration.of(ttl, ChronoUnit.SECONDS));
   }

   @Override
   public boolean isBlacklisted(String key) {
      final String jti = jwtService.extractJti(key)
              .orElseThrow(() -> new ExtractClaimException("JTI not found in token."));
      return tokensRedisTemplate.hasKey(jti);
   }
}
