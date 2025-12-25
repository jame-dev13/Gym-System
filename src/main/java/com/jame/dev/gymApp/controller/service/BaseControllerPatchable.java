package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public abstract non-sealed class BaseControllerPatchable<E, DTO_IN, DTO_OUT> extends BaseControllerCommon<E, DTO_IN, DTO_OUT> {

   private final CRUDServiceServicePatch<E, DTO_IN, Long> patchService;

   protected BaseControllerPatchable(final BaseCrudService<E, DTO_IN, Long> service,
                                  final AppCacheService<DTO_OUT> cache,
                                  final BaseMapper<E, DTO_OUT> mapper,
                                  final String key,
                                  final Function<E, Long> idExtractor,
                                  final CRUDServiceServicePatch<E, DTO_IN, Long> patchService) {
      super(service, cache, mapper, key, idExtractor);
      this.patchService = patchService;
   }

   protected ResponseEntity<@NonNull DTO_OUT> patch(final long id){
      final E entity = this.patchService.patch(id);
      final DTO_OUT dto = super.mapper.toDto(entity);
      super.invalidateIfExists();
      return ResponseEntity.ok(dto);
   }
}
