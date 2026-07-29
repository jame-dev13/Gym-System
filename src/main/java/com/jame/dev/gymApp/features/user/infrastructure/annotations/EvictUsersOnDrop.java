package com.jame.dev.gymApp.features.user.infrastructure.annotations;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.application.model.CacheValues.*;
import static com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues.DOWNING_CUSTOMERS;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(value = {USERS, CUSTOMERS, DOWNING_CUSTOMERS}, allEntries = true),
   @CacheEvict(value = USER, key = "#id")
})
public @interface EvictUsersOnDrop {
}
