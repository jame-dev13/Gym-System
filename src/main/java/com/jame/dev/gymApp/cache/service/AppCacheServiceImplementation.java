package com.jame.dev.gymApp.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.exception.CacheKeyNotExistsException;
import com.jame.dev.gymApp.exception.EmptyCacheObjectException;
import com.jame.dev.gymApp.model.dto.out.PageMetaData;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import redis.clients.jedis.JedisPooled;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class AppCacheServiceImplementation<T> implements AppCacheService<T> {
   private Class<T> type;
   private final JedisPooled cacheAppPool;
   private final ObjectMapper mapper =
           new ObjectMapper().registerModule(new JavaTimeModule());

   public AppCacheServiceImplementation(Class<T> type, JedisPooled cacheAppPool) {
      this.type = type;
      this.cacheAppPool = cacheAppPool;
   }

   private final Function<String, T> mapperHelper = s -> {
      try {
         return mapper.readValue(s, type);
      } catch (Exception e) {
         throw new RuntimeException("Error parsing JSON: " + s, e);
      }
   };

   private boolean matchId(final String line, final long id) {
      try {
         JsonNode json = mapper.readTree(line);
         return json.path("id").asLong() == id;
      } catch (JsonProcessingException ignored) {
         return false;
      }
   }


   @Override
   public Optional<Page<@NonNull T>> getCache(String key) {
      List<String> list = cacheAppPool.lrange(key, 0, -1);
      if (list == null || list.isEmpty()) return Optional.empty();

      Collections.reverse(list);
      final List<T> pageContent = list.stream()
              .map(mapperHelper)
              .toList();

      final String jsonMetaData = cacheAppPool.get(key.concat(":meta"));
      final Optional<PageMetaData> optionalMetadata = deserializeMetaDataJson(jsonMetaData);

      if (optionalMetadata.isEmpty()) return Optional.empty();

      final PageMetaData metadata = optionalMetadata.get();
      final Sort sort = metadata.sortProperty() == null
              ? Sort.unsorted()
              : Sort.by(Sort.Direction.fromString(metadata.sortDirection()), metadata.sortProperty());

      final Page<@NonNull T> page = new PageImpl<>(pageContent,
              PageRequest.of(metadata.number(), metadata.size(), sort),
              metadata.totalElements());

      return Optional.of(page);
   }

   @Override
   public void saveCache(final String key, final Page<@NonNull T> page) {
      final long exp = 420;
      try {
         cacheAppPool.del(key);
         for (T type : page.getContent()) {
            cacheAppPool.rpush(key, mapper.writeValueAsString(type));
         }
         final PageMetaData metaData = getPageMetaDataHelper(page);
         final String metaSerialized = mapper.writeValueAsString(metaData);
         cacheAppPool.expire(key, exp);
         cacheAppPool.setex(key.concat(":meta"), exp, metaSerialized);
      } catch (JsonProcessingException e) {
         log.error("Error processing Json: ", e);
      }
   }

   //remove
   @Override
   public Optional<T> get(final String key, final long id) {
      if (!cacheAppPool.exists(key)) {
         throw new CacheKeyNotExistsException("Key: " + key + " doesn't exists.");
      }

      final List<String> cacheJson = cacheAppPool.lrange(key, 0, -1);
      if (cacheJson.isEmpty()) {
         throw new EmptyCacheObjectException("No cache associated with key: " + key);
      }
      return getItem(cacheJson, id);
   }

   private Optional<T> getItem(List<String> jsonList, long id){
      for (String json : jsonList) {
         T item = mapperHelper.apply(json);
         if(matchId(json, id))
            return Optional.of(item);
      }
      return Optional.empty();
   }

   @Override
   public void invalidatePage(final String key) {
      if (cacheAppPool.exists(key))
         cacheAppPool.del(key);
   }

   @Override
   public boolean keyExists(final String key) {
      return cacheAppPool.exists(key);
   }


   private PageMetaData getPageMetaDataHelper(final Page<@NonNull T> page) {
      Sort sort = page.getSort();
      var order = sort.stream().findFirst().orElse(null);
      final String sortProperty = (order != null) ? order.getProperty() : "id";
      final String sortDirection = (order != null) ? order.getDirection().name(): "ASC";

      return new PageMetaData(
              page.getNumber(), page.getSize(),
              page.getTotalElements(), page.getTotalPages(),
              sortProperty, sortDirection
      );
   }

   private Optional<PageMetaData> deserializeMetaDataJson(final String json) {
      try {
         return Optional.of(mapper.readValue(json, PageMetaData.class));
      } catch (JsonProcessingException e) {
         log.error("Error deserializing json object.", e);
         return Optional.empty();
      }
   }
}
