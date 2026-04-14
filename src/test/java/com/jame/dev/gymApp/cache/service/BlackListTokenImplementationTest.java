package com.jame.dev.gymApp.cache.service;

import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.exception.TokenAlreadyBlacklistedException;
import com.jame.dev.gymApp.jwt.service.JwtService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BlackListTokenImplementationTest {

   @Mock StringRedisTemplate tokensRedisTemplate;
   @Mock JwtService jwtService;
   @Mock ValueOperations<String, String> valueOperations;

   @InjectMocks BlackListTokenImplementation service;

   @Nested
   @DisplayName("Tests for blacklistToken method using JTI")
   class BlacklistTokenTests {

      @Test
      @DisplayName("Should successfully blacklist token when JTI is valid and not present in Redis")
      void blacklistTokenShouldSucceedWhenJtiIsNew() {
         String token = "valid.jwt.token";
         String jti = "uuid-1234-5678";
         Date expirationDate = new Date(System.currentTimeMillis() + 3600000); // 1 hour later

         given(jwtService.extractJti(token)).willReturn(Optional.of(jti));
         given(tokensRedisTemplate.hasKey(jti)).willReturn(FALSE);
         given(jwtService.extractExpiration(token)).willReturn(Optional.of(expirationDate));
         given(tokensRedisTemplate.opsForValue()).willReturn(valueOperations);

         assertDoesNotThrow(() -> service.blacklistToken(token));

         verify(jwtService).extractJti(token);
         verify(tokensRedisTemplate).hasKey(jti);
         verify(jwtService).extractExpiration(token);
         verify(valueOperations).set(eq(jti), eq("blacklisted"), any(Duration.class));
      }

      @Test
      @DisplayName("Should throw ExtractClaimException when JTI cannot be extracted")
      void blacklistTokenShouldThrowExceptionWhenJtiIsMissing() {
         String token = "invalid.token";
         given(jwtService.extractJti(token)).willReturn(Optional.empty());

         assertThrowsExactly(ExtractClaimException.class, () -> service.blacklistToken(token));

         verify(jwtService).extractJti(token);
         verifyNoInteractions(tokensRedisTemplate);
      }

      @Test
      @DisplayName("Should throw TokenAlreadyBlacklistedException when JTI already exists in Redis")
      void blacklistTokenShouldThrowExceptionWhenJtiIsAlreadyPresent() {
         String token = "valid.token";
         String jti = "already-exists-id";

         given(jwtService.extractJti(token)).willReturn(Optional.of(jti));
         given(tokensRedisTemplate.hasKey(jti)).willReturn(TRUE);

         assertThrowsExactly(TokenAlreadyBlacklistedException.class, () -> service.blacklistToken(token));

         verify(tokensRedisTemplate).hasKey(jti);
         verifyNoMoreInteractions(jwtService);
      }

      @Test
      @DisplayName("Should throw ExtractClaimException when expiration cannot be extracted")
      void blacklistTokenShouldThrowExceptionWhenExpirationIsMissing() {
         String token = "valid.token";
         String jti = "uuid-jti";

         given(jwtService.extractJti(token)).willReturn(Optional.of(jti));
         given(tokensRedisTemplate.hasKey(jti)).willReturn(FALSE);
         given(jwtService.extractExpiration(token)).willReturn(Optional.empty());

         assertThrowsExactly(ExtractClaimException.class, () -> service.blacklistToken(token));

         verify(jwtService).extractExpiration(token);
         verifyNoInteractions(valueOperations);
      }
   }

   @Nested
   @DisplayName("Tests for isBlacklisted method using JTI")
   class IsBlacklistedTests {

      @Test
      @DisplayName("Should return true when JTI is found in Redis")
      void isBlacklistedShouldReturnTrueWhenJtiExists() {
         String token = "valid.token";
         String jti = "existing-jti";

         given(jwtService.extractJti(token)).willReturn(Optional.of(jti));
         given(tokensRedisTemplate.hasKey(jti)).willReturn(TRUE);

         boolean result = service.isBlacklisted(token);

         assertTrue(result);
         verify(jwtService).extractJti(token);
         verify(tokensRedisTemplate).hasKey(jti);
      }

      @Test
      @DisplayName("Should throw ExtractClaimException when JTI extraction fails during check")
      void isBlacklistedShouldThrowExceptionWhenJtiExtractionFails() {
         String token = "bad.token";
         given(jwtService.extractJti(token)).willReturn(Optional.empty());

         assertThrowsExactly(ExtractClaimException.class, () -> service.isBlacklisted(token));

         verifyNoInteractions(tokensRedisTemplate);
      }
   }
}