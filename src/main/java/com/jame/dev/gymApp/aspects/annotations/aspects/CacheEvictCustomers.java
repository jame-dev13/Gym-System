package com.jame.dev.gymApp.aspects.annotations.aspects;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.shared.enums.CacheValues.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented()
@Caching(evict = {
   @CacheEvict(
      value = {
         CUSTOMERS,
         SUBSCRIPTIONS,
         SUBSCRIPTION
      },
      allEntries = true,
      cacheManager = "redisCacheManager",
      beforeInvocation = true
   ),
   @CacheEvict(
      value = CUSTOMER,
      key = "#id",
      allEntries = true,
      cacheManager = "redisCacheManager",
      beforeInvocation = true
   )
})
public @interface CacheEvictCustomers {
}
