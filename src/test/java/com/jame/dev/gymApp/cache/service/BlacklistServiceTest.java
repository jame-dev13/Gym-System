package com.jame.dev.gymApp.cache.service;

import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.JedisPooled;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlacklistServiceTest {

   @Mock
   private JedisPooled tokensPool;

   @Mock
   private JwtService jwtService;

   @InjectMocks
   private TokenServiceImplementation service;

   private final String TOKEN = "TOKEN-JWT-TEST";

   @Test
   @DisplayName("Token Blacklisted")
   void blacklistToken() {
      when(tokensPool.exists(TOKEN)).thenReturn(false);
      when(jwtService.extractExpiration(TOKEN))
              .thenReturn(Optional.of(new Date(anyLong())));

      service.blacklistToken(TOKEN);

      verify(tokensPool).exists(TOKEN);
      verify(tokensPool).setex(eq(TOKEN), anyLong(), eq("blacklisted"));
   }

   @Test
   @DisplayName("Token not blacklisted")
   void failBlacklistToken(){
      when(tokensPool.exists(TOKEN)).thenReturn(true);
      Assertions.assertThrows(TokenAlreadyBlacklistedException.class,
              () -> service.blacklistToken(TOKEN),
              "Should throws the exception.");
      verify(tokensPool).exists(TOKEN);
   }

   @Test
   @DisplayName("Check if token is blacklisted.")
   void isBlacklisted() {
      when(tokensPool.exists(TOKEN)).thenReturn(true);
      boolean isBlackListed = service.isBlacklisted(TOKEN);
      Assertions.assertTrue(isBlackListed, "Should be blacklisted.");
   }
}