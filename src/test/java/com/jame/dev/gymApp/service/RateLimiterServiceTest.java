package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.features.auth.domain.exception.TemporaryBlockedException;
import com.jame.dev.gymApp.features.auth.domain.exception.TooManyRequestsException;
import com.jame.dev.gymApp.features.auth.application.contract.BlockingService;
import com.jame.dev.gymApp.features.auth.infrastructure.rate_limiting.RateLimiterApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RateLimiterServiceTest {

   @Mock
   StringRedisTemplate fixedListTemplate;
   @Mock
   BlockingService blockingService;
   @Mock
   ValueOperations<String, String> valueOperations;

   @InjectMocks
   RateLimiterApplicationService service;

   @Test
   @DisplayName("Should throws TooManyRequestException and block user when limit is exceeded")
   void shouldThrowsTooManyRequestException() {
      long requestCount = 6;
      String ip = "127.0.0.1";
      var request = mock(HttpServletRequest.class);
      given(request.getRemoteAddr()).willReturn(ip);

      given(fixedListTemplate.opsForValue()).willReturn(valueOperations);
      given(valueOperations.increment(anyString())).willReturn(requestCount);

      given(blockingService.isBlocked(anyString())).willReturn(false);

      willDoNothing().given(blockingService).blockTemporary(anyString(), anyString());

      assertThrows(TooManyRequestsException.class, () -> service.fixedWindow(request));

      verify(blockingService).isBlocked(anyString());
      verify(fixedListTemplate).opsForValue();
      verify(valueOperations).increment(anyString());
      verify(blockingService).blockTemporary(anyString(), anyString());

      verifyNoMoreInteractions(blockingService, fixedListTemplate, valueOperations);
   }

   @Test
   @DisplayName("Should throws TemporaryBlockedException")
   void shouldThrowsTemporaryBlockedException() {
      String ip = "127.0.0.1";
      var request = mock(HttpServletRequest.class);
      given(request.getRemoteAddr()).willReturn(ip);

      given(blockingService.isBlocked(anyString()))
              .willReturn(true);
      given(blockingService.getBlockingTimeOf(anyString()))
              .willReturn(Duration.ofMinutes(5));

      assertThrows(TemporaryBlockedException.class, () -> service.fixedWindow(request));

      verify(blockingService).isBlocked(anyString());
      verify(blockingService).getBlockingTimeOf(anyString());
      verifyNoMoreInteractions(blockingService);
      verifyNoInteractions(fixedListTemplate, valueOperations);
   }

   @Test
   @DisplayName("Should sets the key and expiration without throwing anything")
   void shouldStoreTheKey() {
      String ip = "127.0.0.1";
      var request = mock(HttpServletRequest.class);

      given(request.getRemoteAddr()).willReturn(ip);
      given(fixedListTemplate.opsForValue())
              .willReturn(valueOperations);
      given(valueOperations.increment(anyString()))
              .willReturn(1L);
      given(blockingService.isBlocked(anyString()))
              .willReturn(false);
      given(fixedListTemplate.expire(anyString(), any(Duration.class)))
              .willReturn(true);

      assertDoesNotThrow(() -> service.fixedWindow(request));

      verify(fixedListTemplate).opsForValue();
      verify(valueOperations).increment(anyString());
      verify(blockingService).isBlocked(anyString());
      verify(fixedListTemplate).expire(anyString(), any(Duration.class));
      verifyNoMoreInteractions(blockingService, fixedListTemplate, valueOperations);
   }

}
