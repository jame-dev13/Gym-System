package com.jame.dev.gymApp.features.customer.infrastructure.annotations;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMERS;
import static com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues.JOINING_CUSTOMERS;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(
   evict = @CacheEvict(value = {CUSTOMERS, JOINING_CUSTOMERS}, allEntries = true)
)
public @interface EvictOnSaveCustomers {
}
