package com.jame.dev.gymApp.features.subscription.infrastructure.annotations;

import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.metrics.infrastructure.cache.CacheEvolutionMetricsValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTION;
import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTIONS;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(value = { SUBSCRIPTIONS, CacheEvolutionMetricsValues.DOWNING_SUBSCRIBERS, CacheValues.PAYMENTS }, allEntries = true),
   @CacheEvict(value = SUBSCRIPTION, keyGenerator = "authCurrentKeyGen")
})
public @interface EvictCurrentOnUpdateSub {
}
