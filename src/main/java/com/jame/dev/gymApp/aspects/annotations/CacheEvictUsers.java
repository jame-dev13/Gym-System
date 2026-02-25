package com.jame.dev.gymApp.aspects.annotations;

import com.jame.dev.gymApp.shared.enums.CacheValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented()
@Caching(evict = {
        @CacheEvict(
                value = {CacheValues.USERS, CacheValues.CUSTOMERS},
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
