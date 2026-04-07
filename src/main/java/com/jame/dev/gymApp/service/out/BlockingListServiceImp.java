package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.service.in.BlockingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BlockingListServiceImp implements BlockingService {
   private final StringRedisTemplate blockingListTemplate;

   private static final int BASE_BLOCK_MINUTES = 5;
   private static final int BASE_TTL_STRIKE_HOURS = 24;

   @Override
   public void blockTemporary(final String blockingKey, final String strikeKey) {
      final Long currentStrikes = blockingListTemplate.opsForValue().increment(strikeKey);

      if (currentStrikes != null && currentStrikes == 1) {
         blockingListTemplate.expire(strikeKey, Duration.ofHours(BASE_TTL_STRIKE_HOURS));
      }

      final long blockDurationMinutes =
              (long) BASE_BLOCK_MINUTES * (currentStrikes != null ? currentStrikes : 1);

      blockingListTemplate.opsForValue().set(
              blockingKey,
              "Temporary Locked.",
              Duration.ofMinutes(blockDurationMinutes));
   }

   @Override
   public boolean isBlocked(final String blockingKey) {
      return blockingListTemplate.hasKey(blockingKey);
   }

   @Override
   public Duration getBlockingTimeOf(String blockingKey) {
      final long expiration = blockingListTemplate.getExpire(blockingKey);
      return expiration > 0 ? Duration.ofMinutes(expiration) : Duration.ZERO;
   }
}
