package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.exception.NoCacheObjectFoundException;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.CRUDService;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Optional;

public abstract class BaseController<E, DTO_IN, DTO_OUT> {

   protected final CRUDService<E, DTO_IN> service;
   protected final BaseMapper<E, DTO_OUT> mapper;
   private final AppCacheService<DTO_OUT> cache;
   private final String key;
   private String currentPageKey;


   protected BaseController(final CRUDService<E, DTO_IN> service,
                            final BaseMapper<E, DTO_OUT> mapper,
                            final AppCacheService<DTO_OUT> cache,
                            final String key) {
      this.service = service;
      this.mapper = mapper;
      this.cache = cache;
      this.key = key;
   }

   protected ResponseEntity<@NonNull DTO_OUT> create(@NonNull final DTO_IN dto, String location) {
      E entity = service.save(dto);
      DTO_OUT dtoOut = mapper.toDto(entity);

      final Long id = extractId(entity);
      final URI created = URI.create(location + "/" + id);
      return ResponseEntity.created(created)
              .body(dtoOut);
   }

   protected ResponseEntity<@NonNull Page<@NonNull DTO_OUT>> getPage(int page, int size) {
      final String keyFormated = String.format("%s:%d:%d", key, page, size);
      this.currentPageKey = keyFormated;
      final Optional<Page<@NonNull DTO_OUT>> cachePage = cache.getCache(keyFormated);
      if (cachePage.isPresent()) {
         return ResponseEntity.ok()
                 .body(cachePage.get());
      }
      final Pageable pageable = PageRequest.of(page, size);
      final Page<@NonNull E> pageEntity = service.getPageOfActives(pageable);
      final Page<@NonNull DTO_OUT> pageDto = pageEntity.map(mapper::toDto);

      cache.saveCache(keyFormated, pageDto);

      return ResponseEntity
              .ok()
              .body(pageDto);
   }

   protected ResponseEntity<@NonNull DTO_OUT> getOne(long id) {
      final String key = this.currentPageKey;
      if (cache.keyExists(key)) {
         final DTO_OUT dto = cache.get(key, id)
                 .orElseThrow(() -> new NoCacheObjectFoundException("Cache item not found."));
         return ResponseEntity.ok().body(dto);
      }
      final E entity = service.getById(id)
              .orElseThrow(() -> new EntityNotFoundException(id + ": Not found."));
      final DTO_OUT dto = mapper.toDto(entity);
      return ResponseEntity.ok()
              .body(dto);
   }

   protected ResponseEntity<@NonNull DTO_OUT> put(long id, @NonNull final DTO_IN dto) {
      final E entity = service.update(id, dto);
      final DTO_OUT dtoResponse = mapper.toDto(entity);
      invalidateIfExists();
      return ResponseEntity.ok()
              .body(dtoResponse);
   }

   protected ResponseEntity<Void> delete(long id) {
      service.softDeleteById(id);
      invalidateIfExists();
      return ResponseEntity.noContent().build();
   }

   protected ResponseEntity<@NonNull DTO_OUT> ok(E entity) {
      invalidateIfExists();
      return ResponseEntity.ok().body(mapper.toDto(entity));
   }

   private Long extractId(E entity) {
      try {
         Field idField = entity.getClass().getDeclaredField("id");
         idField.setAccessible(true);
         return (Long) idField.get(entity);
      } catch (Exception e) {
         throw new RuntimeException("Can't extract the id: " + e.getMessage(), e);
      }
   }

   private void invalidateIfExists() {
      if (!cache.keyExists(currentPageKey))
         return;
      cache.invalidatePage(currentPageKey);
      this.currentPageKey = "";
   }
}
