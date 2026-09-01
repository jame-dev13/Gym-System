package com.jame.dev.gymApp.features.backup.infrastructure.annotations;

import com.jame.dev.gymApp.features.backup.infrastructure.cache.CacheBackupValues;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Caching(
   evict = @CacheEvict(value = CacheBackupValues.CACHE_BACKUP, allEntries = true)
)
public @interface EvictBackups {
}
