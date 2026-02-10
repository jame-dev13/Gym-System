package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.cache.service.LruCache;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class BaseControllerCommon<E, DTO_IN, DTO_OUT> {

   protected final BaseCrudService<E, DTO_IN, Long> service;
   protected final AppCacheService<DTO_OUT> cache;
   protected final BaseMapper<E, DTO_OUT> mapper;
   private final String key;
   private final Function<E, Long> idExtractor;
   private String currentPageKey;
   private final int CACHE_SIZE = 5;
   private final Map<String, DTO_OUT> cacheOnes = Collections
           .synchronizedMap(new LruCache<>(CACHE_SIZE));


   protected ResponseEntity<@NonNull Page<@NonNull DTO_OUT>> getPage(int page, int size) {
      this.currentPageKey = "%s:%d:%d".formatted(key, page, size);
      final Optional<Page<@NonNull DTO_OUT>> cachePage = cache.getCache(this.currentPageKey);
      if (cachePage.isPresent()) {
         return ResponseEntity.ok()
                 .body(cachePage.get());
      }
      final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
      final Page<@NonNull E> entityPage = service.getPage(pageable);
      final Page<@NonNull DTO_OUT> dtoPage = entityPage.map(mapper::toDto);

      cache.saveCache(this.currentPageKey, dtoPage);

      return ResponseEntity.ok(dtoPage);
   }

   protected ResponseEntity<@NonNull DTO_OUT> getOne(long id) {
      final String key = "%s:id:%d".formatted(this.key, id);
      if (cacheOnes.containsKey(key)) {
         final DTO_OUT dto = cacheOnes.get(key);
         return ResponseEntity.ok(dto);
      }
      final E entity = service.getById(id)
              .orElseThrow(() -> new EntityNotFoundException(id + ": Not found."));
      final DTO_OUT dto = mapper.toDto(entity);
      cacheOnes.put(key, dto);
      return ResponseEntity.ok(dto);
   }


   protected ResponseEntity<@NonNull DTO_OUT> create(@NonNull final DTO_IN dto, String location) {
      this.invalidateIfExists();
      final E entity = service.save(dto);
      final DTO_OUT dtoOut = mapper.toDto(entity);
      final long id = extractId(entity);
      final URI created = URI.create(location + "/" + id);
      cacheOnes.put("%s:id:%d".formatted(key, id), dtoOut);
      return ResponseEntity.created(created)
              .body(dtoOut);
   }

   protected ResponseEntity<@NonNull DTO_OUT> update(final long id, @NonNull final DTO_IN dto) {
      this.invalidateIfExists();
      final E entity = service.update(id, dto);
      final DTO_OUT dtoOut = mapper.toDto(entity);
      return ResponseEntity.ok(dtoOut);
   }

   protected ResponseEntity<Void> delete(long id) {
      invalidateIfExists();
      service.softDelete(id);
      return ResponseEntity.noContent().build();
   }

   private long extractId(E entity) {
      return this.idExtractor.apply(entity);
   }

   protected void invalidateIfExists() {
      if (this.currentPageKey != null && cache.keyExists(this.currentPageKey)) {
         cache.invalidatePage(this.currentPageKey);
         this.currentPageKey = "";
      }
      cacheOnes.clear();
   }
}
