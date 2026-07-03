package com.jame.dev.gymApp.features.metrics.infrastructure.annotations;

import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CachePaymentMetricsValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(value = CachePaymentMetricsValues.INVESTMENT, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CachePaymentMetricsValues.EVOLUTION, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CachePaymentMetricsValues.RESUME, allEntries = true, cacheManager = "redisCacheManager")
})
public @interface EvictPaymentMetrics {
}
