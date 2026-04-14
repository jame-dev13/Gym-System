package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import com.jame.dev.gymApp.service.common.FullService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public abstract class FullControllerIdentifiable<E, OUT, IN> extends
   BaseController<OUT, IN> {

   private final FullService<OUT, IN> fullService;
   private final EmailIdentifiable<E> identifiable;
   private final BaseMapper<E, OUT> mapper;

   protected FullControllerIdentifiable(
      FullService<OUT, IN> service,
      Function<OUT, Long> idExtractor,
      EmailIdentifiable<E> identifiable,
      BaseMapper<E, OUT> mapper) {
      super(service, idExtractor);
      this.fullService = service;
      this.identifiable = identifiable;
      this.mapper = mapper;
   }


   public ResponseEntity<OUT> getByEmail(
      @EmailValid final String email) {
      final E entity = identifiable.getByEmail(email)
         .orElseThrow(() -> new EntityNotFoundException("Not found."));
      final OUT dto = mapper.toDto(entity);
      return ResponseEntity.ok(dto);
   }

   protected ResponseEntity<@NonNull OUT> patch(
      @Positive(message = "Value must be positive integer and non cero.") final long id) {
      final OUT response = fullService.patch(id);
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<@NonNull OUT> put(
      @Positive(message = "Value must be positive integer and non cero.") final long id,
      @Valid
      @NotNullObject(message = "Payload must not be null.") final IN dto) {
      final OUT response = fullService.put(id, dto);
      return ResponseEntity.ok(response);
   }

}
