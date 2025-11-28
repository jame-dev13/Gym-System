package com.jame.dev.gymApp.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jame.dev.gymApp.exception.EmptyCacheObjectException;
import com.jame.dev.gymApp.exception.IndexNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public class AppCacheServiceImplementation<T> implements AppCacheService<T> {
   private final Class<T> type;
   private final JedisPooled cacheAppPool;
   private final ObjectMapper mapper;

   private final Function<String, T> mapperHelper = s -> {
      try {
         return mapper.readValue(s, type);
      } catch (Exception e) {
         throw new RuntimeException("Error parsing JSON: " + s, e);
      }
   };

   @Override
   public List<T> getCache(String key) {
      List<String> list = cacheAppPool.lrange(key, 0, -1);
      if (list == null || list.isEmpty()) {
         throw new EmptyCacheObjectException("Cache list is empty.");
      }
      Collections.reverse(list);
      return list.stream()
              .map(mapperHelper)
              .toList();
   }

   @Override
   public void saveCache(String key, List<T> t) {
      try {
         cacheAppPool.del(key);
         for (T type : t) {
            cacheAppPool.rpush(key, mapper.writeValueAsString(type));
         }
         cacheAppPool.expire(key, 420);
      } catch (JsonProcessingException e) {
         log.error("Error processing Json: ", e);
      }
   }

   @Override
   public void addToCache(String key, T t) {
      try {
         if (!cacheContainsHelper(key, t)) {
            cacheAppPool.rpush(key, mapper.writeValueAsString(t));
         }
      } catch (JsonProcessingException e) {
         log.error("Error processing Json: ", e);
      }
   }

   @Override
   public void updateItemInCache(String key, Predicate<T> filter, T item) {
      try {
         List<T> items = cacheAppPool.lrange(key, 0, -1)
                 .stream()
                 .map(mapperHelper)
                 .toList();
         int index = getIndexTwoPointers(items, filter);
         if(index == -1){
            throw new IndexNotFoundException("Item not found.");
         }
         String value = mapper.writeValueAsString(item);
         cacheAppPool.lset(key, index, value);
         long ttl = cacheAppPool.expireTime(key);
         cacheAppPool.expire(key, (ttl <= 0) ? 420 : ttl);
      } catch (JsonProcessingException e) {
         log.error("Error processing Json: ", e);
      }
   }

   private boolean cacheContainsHelper(final String key, T t) throws JsonProcessingException {
      List<String> cache = cacheAppPool.lrange(key, 0, -1);
      return cache.contains(mapper.writeValueAsString(t));
   }

   private int getIndexLinear(List<T> items, Predicate<T> test) throws JsonProcessingException {
      for (int i = 0; i < items.size(); i++) {
         T item = items.get(i);
         if(test.test(item)){
            return i;
         }
      }
      return -1;
   }

   private int getIndexTwoPointers(List<T> items, Predicate<T> test) {
      int front = 0, rear = items.size() - 1;

      while (front <= rear) {
         if (test.test(items.get(rear))) {
            return rear;
         }
         if (test.test(items.get(front))) {
            return front;
         }
         front++;
         rear--;
      }
      return -1;
   }
}
