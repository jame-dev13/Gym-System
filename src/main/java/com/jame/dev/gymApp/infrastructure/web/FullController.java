package com.jame.dev.gymApp.infrastructure.web;

import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.application.contract.FullService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.function.Function;

@Validated
public abstract class FullController<OUT, IN> extends
   BaseController<OUT, IN> {
   private final FullService<OUT, IN> fullService;

   public FullController(
      FullService<OUT, IN> service,
      Function<OUT, Long> idExtractor) {
      super(service, idExtractor);
      this.fullService = service;
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
