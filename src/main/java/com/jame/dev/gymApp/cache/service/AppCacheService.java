package com.jame.dev.gymApp.cache.service;

import lombok.NonNull;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface AppCacheService<T> {

   Optional<Page<@NonNull T>> getCache(final String key);

   void saveCache(final String key, Page<@NonNull T> t);

   void invalidatePage(final String key);

   boolean keyExists(final String key);
}
