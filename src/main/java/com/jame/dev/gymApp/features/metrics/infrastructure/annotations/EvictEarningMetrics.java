package com.jame.dev.gymApp.features.metrics.infrastructure.annotations;


import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheMetricValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(
   evict = @CacheEvict(
      value = CacheMetricValues.EARNINGS,
      allEntries = true,
      cacheManager = "redisCacheManager"))
public @interface EvictEarningMetrics {
}
