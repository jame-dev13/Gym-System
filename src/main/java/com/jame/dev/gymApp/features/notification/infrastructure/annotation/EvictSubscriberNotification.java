package com.jame.dev.gymApp.features.notification.infrastructure.annotation;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.features.notification.infrastructure.cache.SubscriberNotificationCacheValues.VALUE;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
@Inherited
@Caching(
   evict = @CacheEvict(value = VALUE, key = "#uuid")
)
public @interface EvictSubscriberNotification {
}
