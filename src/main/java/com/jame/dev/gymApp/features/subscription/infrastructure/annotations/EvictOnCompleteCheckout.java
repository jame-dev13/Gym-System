package com.jame.dev.gymApp.features.subscription.infrastructure.annotations;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.application.model.CacheValues.*;
import static com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues.BILLINGS;
import static com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheMetricValues.EARNINGS;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(value = {
      SUBSCRIPTIONS, SUBSCRIPTION, PAYMENTS,
      PAYMENT_METRICS, BILLINGS, EARNINGS
   }, allEntries = true)
})
public @interface EvictOnCompleteCheckout {
}
