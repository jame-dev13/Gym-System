package com.jame.dev.gymApp.features.notification.infrastructure.annotation;

import com.jame.dev.gymApp.features.notification.infrastructure.cache.SubscriberNotificationCacheValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
@Caching(
   evict = @CacheEvict(
      value = SubscriberNotificationCacheValues.VALUE,
      keyGenerator = "authPrincipalCurrentKeyGen")
)
public @interface EvictSubscriberNotification {
}
