package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.model.dto.out.CacheMutated;
import com.jame.dev.gymApp.service.out.RedisCacheInvalidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersCacheInvalidationListener {
   private final RedisCacheInvalidationService cacheInvalidationService;

   @EventListener
   public void invalidate(final CacheMutated cacheMutated) {
      cacheInvalidationService.deleteByPrefix(cacheMutated.cacheKey());
      cacheInvalidationService.deleteByPrefix("customers");
   }
}
