package com.jame.dev.gymApp.cache.service;

import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenServiceImplementation implements BlacklistService {

   private final JedisPooled tokensPool;
   private final JwtService jwtService;

   @Override
   public void blacklistToken(String key) {
      if(tokensPool.exists(key)){
         throw new TokenAlreadyBlacklistedException("Token already blacklisted.");
      }
      final Date expiration = jwtService.extractExpiration(key)
              .orElseThrow(() -> new ExtractClaimException("Can't extract claims."));
      final long ttl = Duration.between(Instant.now(), expiration.toInstant()).getSeconds();
      if(ttl <= 0) return;
      tokensPool.setex(key, ttl, "blacklisted");
   }

   @Override
   public boolean isBlacklisted(String key) {
      return tokensPool.exists(key);
   }
}
