package com.jame.dev.gymApp.service;


import com.jame.dev.gymApp.service.out.BlockingListServiceImp;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockingServiceTest {

   @Mock
   StringRedisTemplate blockingListTemplate;

   @Mock
   ValueOperations<String, String> valueOperations;

   @InjectMocks
   BlockingListServiceImp service;

   @Test
   @DisplayName("Should block a given blockingKey")
   void shouldBlockGivenKey() {
      String blockingKey = "rl:auth:127.0.0.1";
      String strikeKey = blockingKey + ":struck";

      given(blockingListTemplate.opsForValue()).willReturn(valueOperations);
      given(valueOperations.increment(anyString()))
              .willReturn(1L);
      given(blockingListTemplate.expire(anyString(), any(Duration.class)))
              .willReturn(true);
      willDoNothing().given(valueOperations).set(anyString(), any(), any(Duration.class));

      service.blockTemporary(blockingKey, strikeKey);

      verify(blockingListTemplate, atLeast(2)).opsForValue();
      verify(valueOperations).increment(anyString());
      verify(blockingListTemplate).expire(anyString(), any(Duration.class));
      verify(valueOperations).set(anyString(), any(), any(Duration.class));

      verifyNoMoreInteractions(blockingListTemplate, valueOperations);
   }

   @Test
   @DisplayName("Should indicate whether a key is blocked.")
   void shouldIndicatesBlockedStatusOfBlockedKey() {
      String blockingKey = "rl:auth:127.0.0.1";
      given(blockingListTemplate.hasKey(anyString()))
              .willReturn(true);

      assertTrue(service.isBlocked(blockingKey));

      verify(blockingListTemplate).hasKey(anyString());
      verifyNoMoreInteractions(blockingListTemplate);
   }

   @Test
   @DisplayName("Should retrieves the duration from a given key")
   void shouldReturnTheDuration() {
      String blockingKey = "rl:auth:127.0.0.1";
      given(blockingListTemplate.getExpire(anyString()))
              .willReturn(10_000L);
      assertNotNull(service.getBlockingTimeOf(blockingKey));

      verify(blockingListTemplate).getExpire(blockingKey);
   }
}
