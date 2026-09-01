package com.jame.dev.gymApp.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEvictionManager {
   private final CacheManager redisCacheManager;

   public void evictApplicationCaches() {
      redisCacheManager.getCacheNames()
         .stream()
         .map(redisCacheManager::getCache)
         .filter(Objects::nonNull)
         .forEach(Cache::clear);
   }
}
