package com.jame.dev.gymApp.features.customer.infrastructure.annotations;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMER;
import static com.jame.dev.gymApp.application.model.CacheValues.CUSTOMERS;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Caching(evict = {
   @CacheEvict(value = CUSTOMERS, allEntries = true),
   @CacheEvict(value = CUSTOMER, key = "#id"),
   @CacheEvict(value = CUSTOMER, key = "authCurrentKeyGen"),
})
public @interface EvictOnUpdateCustomers {
}
