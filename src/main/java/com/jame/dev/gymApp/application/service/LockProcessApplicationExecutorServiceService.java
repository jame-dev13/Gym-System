package com.jame.dev.gymApp.application.service;

import com.jame.dev.gymApp.application.model.LockProperties;
import com.jame.dev.gymApp.infrastructure.security.lock.LockProcessExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockProcessApplicationExecutorServiceService implements LockProcessExecutorService {
   private final StringRedisTemplate lockTemplate;
   private final LockProperties lockProperties;

   @Override
   public void lock(final String processKey) {
      final String lockKey = lockProperties.key().concat(processKey);
      var setup = lockTemplate.opsForValue().setIfAbsent(lockKey, "lock", Duration.ofMinutes(lockProperties.lifetimeMinutes()));
      log.info("Process locked: {}: {}", lockKey, setup);
   }

   @Override
   public boolean isLocked(final String processKey) {
      final String lockKey = lockProperties.key().concat(processKey);
      return lockTemplate.hasKey(lockKey);
   }

   @Override
   public void releaseLock(final String processKey) {
      final String lockKey = lockProperties.key().concat(processKey);
      lockTemplate.delete(lockKey);
      log.info("Process released: {}", lockKey);
   }
}
