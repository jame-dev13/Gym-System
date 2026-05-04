package com.jame.dev.gymApp.aspects.annotations.aspects;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.shared.enums.CacheValues.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(
      value = SUBSCRIPTIONS,
      allEntries = true,
      beforeInvocation = true,
      cacheManager = "redisCacheManager"),
   @CacheEvict(
      value = SUBSCRIPTION,
      key = "#id",
      cacheManager = "redisCacheManager"
   )
})
public @interface CacheEvictSubscriptions {
}
