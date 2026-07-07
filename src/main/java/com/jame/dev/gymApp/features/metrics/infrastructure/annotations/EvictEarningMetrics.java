package com.jame.dev.gymApp.features.metrics.infrastructure.annotations;


import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEarningMetricValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(value = CacheEarningMetricValues.EARNING_TOTAL, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CacheEarningMetricValues.EARNING_PER_MONTH, allEntries = true, cacheManager = "redisCacheManager"),
   @CacheEvict(value = CacheEarningMetricValues.EARNING_MEMBERSHIP_TYPE, allEntries = true, cacheManager = "redisCacheManager")
})
public @interface EvictEarningMetrics {
}
