package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public abstract class ControllerPutIdentifiable<E, DTO_IN, DTO_OUT>
        extends BaseControllerPutable<E, DTO_IN, DTO_OUT> {
   private final EmailIdentifiable<E> identifiable;

   public ControllerPutIdentifiable(BaseCrudService<E, DTO_IN, Long> service, AppCacheService<DTO_OUT> cache, BaseMapper<E, DTO_OUT> mapper, String key, Function<E, Long> idExtractor, CRUDServiceServicePut<E, DTO_IN, Long> putService, EmailIdentifiable<E> identifiable) {
      super(service, cache, mapper, key, idExtractor, putService);
      this.identifiable = identifiable;
   }

   public ResponseEntity<DTO_OUT> getByEmail(String email) {
      final E entity = identifiable.getByEmail(email)
              .orElseThrow(() -> new EntityNotFoundException("Not found."));
      final DTO_OUT dto = mapper.toDto(entity);
      return ResponseEntity.ok(dto);
   }
}
