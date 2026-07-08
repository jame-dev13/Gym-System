package com.jame.dev.gymApp.features.metrics.infrastructure.annotations;

import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheSubsMetricsValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL_BEFORE, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL_PER_MEMBERSHIP, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CacheSubsMetricsValues.SUBSCRIPTION_TOTAL_PER_MONTH, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CacheSubsMetricsValues.SUBSCRIPTION_RANKING, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CacheSubsMetricsValues.SUBSCRIPTION_PERIOD_RANKING, allEntries = true, cacheManager = "redisCacheManager"),
})
public @interface EvictSubscriptionMetrics {
}
