package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public non-sealed abstract class ControllerPatchPut<E, DTO_IN, DTO_OUT> extends
        BaseControllerCommon<E, DTO_IN, DTO_OUT> {
   private final CRUDServiceServicePatch<E, DTO_IN, Long> patchService;
   private final CRUDServiceServicePut<E, DTO_IN, Long> putService;

   public ControllerPatchPut(BaseCrudService<E, DTO_IN, Long> service, AppCacheService<DTO_OUT> cache, BaseMapper<E, DTO_OUT> mapper, String key, Function<E, Long> idExtractor, CRUDServiceServicePatch<E, DTO_IN, Long> patchService, CRUDServiceServicePut<E, DTO_IN, Long> putService) {
      super(service, cache, mapper, key, idExtractor);
      this.patchService = patchService;
      this.putService = putService;
   }

   protected ResponseEntity<@NonNull DTO_OUT> patch(final long id) {
      final E entity = this.patchService.patch(id);
      final DTO_OUT dto = super.mapper.toDto(entity);
      super.invalidateIfExists();
      return ResponseEntity.ok(dto);
   }

   protected ResponseEntity<@NonNull DTO_OUT> put(long id, @NonNull final DTO_IN dto) {
      final E entity = putService.update(id, dto);
      final DTO_OUT dtoResponse = super.mapper.toDto(entity);
      super.invalidateIfExists();
      return ResponseEntity.ok(dtoResponse);
   }

}
