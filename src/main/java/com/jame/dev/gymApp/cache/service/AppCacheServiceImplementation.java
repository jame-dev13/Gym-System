package com.jame.dev.gymApp.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.PageMetaData;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Deprecated
public class AppCacheServiceImplementation<T> implements AppCacheService<T> {
   private Class<T> type;
   private final JedisPooled cacheAppPool;
   private final ObjectMapper mapper =
           new ObjectMapper().registerModule(new JavaTimeModule());

   public AppCacheServiceImplementation(Class<T> type, JedisPooled cacheAppPool) {
      this.type = type;
      this.cacheAppPool = cacheAppPool;
   }

   @Override
   public Optional<Page<@NonNull T>> getCache(String key) {
      final String json = cacheAppPool.get(key);
      if (json == null || json.isBlank()) {
         return Optional.empty();
      }
      try {
         final JsonNode root = mapper.readTree(json);
         final JsonNode contentNode = root.get("content");
         final List<T> content = mapper.readerForListOf(type).readValue(contentNode);
         final String sortProperty = root.get("sortProperty").asText(Sort.by("id").toString());
         final String sortDirection = root.get("sortDirection").asText(Sort.Direction.ASC.name());
         final Pageable pageable = PageRequest.of(
                 root.get("page").asInt(),
                 root.get("size").asInt(),
                 Sort.by(sortProperty, sortDirection));
         final Page<T> page = new PageImpl<>(
                 content,
                 pageable,
                 root.get("totalElements").asLong()
         );
         return Optional.of(page);
      } catch (IOException e) {
         log.error("Cannot process the object: {}", e.getMessage());
         return Optional.empty();
      }
   }

   @Override
   public void saveCache(final String key, final Page<@NonNull T> page) {
      final long EXP = 300;
      try {
         final PageMetaData metaData = getPageMetaDataHelper(page);
         final PageDto<T> pageDto = new PageDto<>(
                 page.getContent(),
                 metaData.number(), metaData.size(),
                 metaData.totalElements(),
                 metaData.sortProperty(), metaData.sortDirection()
         );
         final String pageSerialized = mapper.writeValueAsString(pageDto);
         cacheAppPool.setex(key, EXP, pageSerialized);
      } catch (JsonProcessingException e) {
         log.error("Error processing Json: ", e);
      }
   }

   @Override
   public void invalidatePage(final String key) {
      cacheAppPool.del(key);
   }

   @Override
   public boolean keyExists(final String key) {
      return cacheAppPool.exists(key);
   }

   private PageMetaData getPageMetaDataHelper(final Page<@NonNull T> page) {
      final Sort sort = page.getSort();
      final Sort.Order order = sort.stream().findFirst().orElse(null);
      final String sortProperty = (order != null) ? order.getProperty() : "id";
      final String sortDirection = (order != null) ? order.getDirection().name() : "ASC";

      return new PageMetaData(
              page.getNumber(), page.getSize(),
              page.getTotalElements(), page.getTotalPages(),
              sortProperty, sortDirection
      );
   }
}
