package com.jame.dev.gymApp.aspects.annotations.aspects;

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
                value = {CacheValues.USERS,
                        CacheValues.CUSTOMERS,
                        CacheValues.SUBSCRIPTIONS},
                allEntries = true,
                cacheManager = "redisCacheManager",
                beforeInvocation = true),
        @CacheEvict(
                value = CacheValues.CUSTOMER,
                key = "#id",
                allEntries = true,
                cacheManager = "redisCacheManager",
                beforeInvocation = true
        )
})
public @interface CacheEvictCustomers {
}
