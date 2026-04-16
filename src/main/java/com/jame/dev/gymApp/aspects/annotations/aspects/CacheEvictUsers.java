package com.jame.dev.gymApp.aspects.annotations.aspects;

import com.jame.dev.gymApp.shared.enums.CacheValues;
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
         CUSTOMER
      },
      allEntries = true,
      cacheManager = "redisCacheManager",
      beforeInvocation = true),
   @CacheEvict(
      value = CacheValues.USER,
      key = "#id",
      allEntries = true,
      cacheManager = "redisCacheManager",
      beforeInvocation = true
   )
})
public @interface CacheEvictUsers {
}
