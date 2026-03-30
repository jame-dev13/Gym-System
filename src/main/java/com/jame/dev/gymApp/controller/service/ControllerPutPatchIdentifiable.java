package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.*;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public abstract class ControllerPutPatchIdentifiable<E, DTO_OUT, DTO_IN> extends
        BaseControllerPatchAndPut<DTO_OUT, DTO_IN> {

   private final EmailIdentifiable<E> identifiable;
   private final BaseMapper<E, DTO_OUT> mapper;

   public ControllerPutPatchIdentifiable(
           final BaseCrudService<DTO_OUT, DTO_IN> service,
           final Function<DTO_OUT, Long> idExtractor,
           final Patchable<DTO_OUT> patchService,
           final Putable<DTO_OUT, DTO_IN> putService,
           final EmailIdentifiable<E> identifiable,
           final BaseMapper<E, DTO_OUT> mapper) {
      super(service, idExtractor, patchService, putService);
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
