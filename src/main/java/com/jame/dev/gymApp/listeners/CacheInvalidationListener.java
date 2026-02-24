package com.jame.dev.gymApp.listeners;

import com.jame.dev.gymApp.service.out.RedisCacheInvalidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CacheInvalidationListener {
   private final RedisCacheInvalidationService cacheInvalidationService;

   @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
   public void onMutation(Object ignored) {
      cacheInvalidationService.deleteByPrefix("users");
      cacheInvalidationService.deleteByPrefix("customers");
      cacheInvalidationService.deleteByPrefix("subscriptions");
   }
}
