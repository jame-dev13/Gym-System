package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.BaseService;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public abstract class ControllerIdentifiable<E, DTO_OUT, DTO_IN>
   extends BaseController<DTO_OUT, DTO_IN> {
   private final EmailIdentifiable<E> identifiable;
   private final BaseMapper<E, DTO_OUT> mapper;

   public ControllerIdentifiable(
      BaseService<DTO_OUT, DTO_IN> service,
      Function<DTO_OUT, Long> idExtractor,
      EmailIdentifiable<E> identifiable,
      BaseMapper<E, DTO_OUT> mapper) {
      super(service, idExtractor);
      this.identifiable = identifiable;
      this.mapper = mapper;
   }

   public ResponseEntity<DTO_OUT> getByEmail(
      @EmailValid final String email) {
      final E entity = identifiable.getByEmail(email)
         .orElseThrow(() -> new EntityNotFoundException("Not found."));
      final DTO_OUT dto = mapper.toDto(entity);
      return ResponseEntity.ok(dto);
   }
}
