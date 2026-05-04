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
      value = {
         USERS,
         CUSTOMERS,
         USERS_INACTIVE,
      },
      allEntries = true,
      beforeInvocation = true,
      cacheManager = "redisCacheManager"
   ),
   @CacheEvict(
      value = USER,
      key = "#id",
      cacheManager = "redisCacheManager"
   )
})
public @interface CacheEvictUsers {
}
