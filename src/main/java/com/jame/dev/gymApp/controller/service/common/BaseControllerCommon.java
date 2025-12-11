package com.jame.dev.gymApp.controller.service.common;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.exception.NoCacheObjectFoundException;
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
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class BaseControllerCommon <E, DTO_IN, DTO_OUT> {

   protected final BaseCrudService<E, DTO_IN, Long> service;
   protected final AppCacheService<DTO_OUT> cache;
   protected final BaseMapper<E, DTO_OUT> mapper;
   private final String key;
   private final Function<E, Long> idExtractor;
   private String currentPageKey;

   protected ResponseEntity<@NonNull Page<@NonNull DTO_OUT>> getPage(int page, int size){
      this.currentPageKey = "%s:%d:%d".formatted(key, page, size);
      Optional<Page<@NonNull DTO_OUT>> cachePage = cache.getCache(this.currentPageKey);
      if(cachePage.isPresent()){
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

   protected ResponseEntity<@NonNull DTO_OUT> create(@NonNull final DTO_IN dto, String location) {
      E entity = service.save(dto);
      DTO_OUT dtoOut = mapper.toDto(entity);

      final long id = extractId(entity);
      final URI created = URI.create(location + "/" + id);
      return ResponseEntity.created(created)
              .body(dtoOut);
   }

   @SuppressWarnings("NullableProblems")
   protected ResponseEntity<Void> delete(long id) {
      service.softDelete(id);
      invalidateIfExists();
      return ResponseEntity.noContent().build();
   }

   private long extractId(E entity){
      return this.idExtractor.apply(entity);
   }

   protected void invalidateIfExists(){
      if(!cache.keyExists(this.currentPageKey)) return;
      cache.invalidatePage(this.currentPageKey);
      this.currentPageKey = "";
   }
}
