package com.jame.dev.gymApp.features.backup.infrastructure.rate_limiting;

import com.jame.dev.gymApp.features.auth.application.contract.BlockingService;
import com.jame.dev.gymApp.features.auth.domain.exception.TemporaryBlockedException;
import com.jame.dev.gymApp.features.auth.domain.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class BackupRateLimiter {

   private final StringRedisTemplate fixedWindowTemplate;
   private final BlockingService blockingService;

   private static final int WINDOW_REQUESTS = 3;
   private static final int WINDOW_SIZE_SECONDS = 3600;

   public void checkRateLimit(final HttpServletRequest request) {
      final String clientIp = request.getRemoteAddr();
      final String key = "rl:backup:" + clientIp;

      if (blockingService.isBlocked(key)) {
         final Duration blockingTime = blockingService.getBlockingTimeOf(key);
         final String timeLeft = String.format("%d:%02d",
            blockingTime.toHours(),
            blockingTime.toMinutesPart());
         throw new TemporaryBlockedException(
            String.format("Backup locked. %s until get unlocked", timeLeft));
      }

      final Long currentCount = fixedWindowTemplate.opsForValue().increment(key);

      if (currentCount != null && currentCount == 1) {
         fixedWindowTemplate.expire(key, Duration.ofSeconds(WINDOW_SIZE_SECONDS));
      }

      if (currentCount != null && currentCount > WINDOW_REQUESTS) {
         final String strikeKey = key + ":struck";
         blockingService.blockTemporary(key, strikeKey);
         throw new TooManyRequestsException("Too many backup requests.");
      }
   }
}
