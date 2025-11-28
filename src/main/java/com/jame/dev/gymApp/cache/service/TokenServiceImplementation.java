package com.jame.dev.gymApp.cache.service;

import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenServiceImplementation implements TokenService {

   private final JedisPooled tokensPool;
   private final JwtService jwtService;

   @Override
   public void blacklistToken(String key) {
      if(tokensPool.exists(key)){
         throw new TokenAlreadyBlacklistedException("Token already blacklisted.");
      }
      Date expiration = jwtService.extractExpiration(key)
              .orElseThrow(() -> new ExtractClaimException("Can't extract claims."));
      long ttl = (expiration.getTime() - System.currentTimeMillis()) / 1000;
      tokensPool.setex(key, ttl, "blacklisted");
   }

   @Override
   public boolean isBlacklisted(String key) {
      return tokensPool.exists(key);
   }
}
