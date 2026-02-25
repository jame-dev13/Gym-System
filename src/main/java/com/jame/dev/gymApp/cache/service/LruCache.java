package com.jame.dev.gymApp.cache.service;

import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated
public class LruCache<K, V> extends LinkedHashMap<K, V> {
   private int capacity;

   public LruCache(int capacity) {
      super(capacity, .75f, true);
      this.capacity = capacity;
   }

   @Override
   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return size() > capacity;
   }
}
