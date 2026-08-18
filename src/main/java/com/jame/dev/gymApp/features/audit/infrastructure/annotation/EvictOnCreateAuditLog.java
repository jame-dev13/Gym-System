package com.jame.dev.gymApp.features.audit.infrastructure.annotation;

import com.jame.dev.gymApp.features.audit.infrastructure.cache.AuditCacheValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Caching(
   evict =
   @CacheEvict(
      value = {AuditCacheValues.AUDIT_LOG_VAL, AuditCacheValues.AUDIT_LOG_CURR_VAL},
      allEntries = true,
      beforeInvocation = true)
)
public @interface EvictOnCreateAuditLog {
}
