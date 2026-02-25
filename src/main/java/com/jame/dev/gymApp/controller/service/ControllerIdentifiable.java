package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public abstract class ControllerIdentifiable<E, DTO_OUT, DTO_IN>
        extends BaseControllerCommon<DTO_OUT, DTO_IN> {
   private final EmailIdentifiable<E> identifiable;
   private final BaseMapper<E, DTO_OUT> mapper;

   public ControllerIdentifiable(BaseCrudService<DTO_OUT, DTO_IN, Long> service, Function<DTO_OUT, Long> idExtractor, EmailIdentifiable<E> identifiable, BaseMapper<E, DTO_OUT> mapper) {
      super(service, idExtractor);
      this.identifiable = identifiable;
      this.mapper = mapper;
   }

   public ResponseEntity<DTO_OUT> getByEmail(String email) {
      final E entity = identifiable.getByEmail(email)
              .orElseThrow(() -> new EntityNotFoundException("Not found."));
      final DTO_OUT dto = mapper.toDto(entity);
      return ResponseEntity.ok(dto);
   }
}
