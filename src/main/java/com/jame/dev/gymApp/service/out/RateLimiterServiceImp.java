package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.exception.TemporaryBlockedException;
import com.jame.dev.gymApp.exception.TooManyRequestsException;
import com.jame.dev.gymApp.service.in.BlockingService;
import com.jame.dev.gymApp.service.in.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterServiceImp implements RateLimiterService {
   private final StringRedisTemplate fixedWindowTemplate;
   private final BlockingService blockingService;

   private static final int WINDOW_REQUESTS = 5;
   private static final int WINDOW_SIZE_SECONDS = 60;

   @Override
   public void fixedWindow(final HttpServletRequest request) {
      final String clientIp = request.getRemoteAddr();

      final String key = "rl:auth:" + clientIp;

      if (blockingService.isBlocked(key)) {
         final Duration blockingTime = blockingService.getBlockingTimeOf(key);

         final String timeLeft = String.format("%d:%02d",
                 blockingTime.toHours(),
                 blockingTime.toMinutesPart());
         throw new TemporaryBlockedException(
                 String.format("Account locked. %s until get unlocked", timeLeft)
         );
      }

      final Long currentCount = fixedWindowTemplate.opsForValue().increment(key);

      if (currentCount != null && currentCount == 1) {
         fixedWindowTemplate.expire(key, Duration.ofSeconds(WINDOW_SIZE_SECONDS));
      }

      if (currentCount != null && currentCount > WINDOW_REQUESTS) {
         final String strikeKey = key + ":struck";
         blockingService.blockTemporary(key, strikeKey);
         throw new TooManyRequestsException("Too many request to the resource.");
      }
   }
}
