package com.jame.dev.gymApp.cache.service;

import java.util.List;
import java.util.function.Predicate;

public interface AppCacheService<T> {
   List<T> getCache(final String key);

   void saveCache(final String key, List<T> t);

   void addToCache(final String key, T item);

   void updateItemInCache(final String key, Predicate<T> filter, T item);
}
