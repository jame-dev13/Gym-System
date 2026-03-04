package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.service.common.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.function.Function;

@Validated
public abstract class BaseControllerPatchAndPut<DTO_OUT, DTO_IN> extends
        BaseControllerCommon<DTO_OUT, DTO_IN> {
   private final Patchable<DTO_OUT> patchService;
   private final Putable<DTO_OUT, DTO_IN> putService;

   public BaseControllerPatchAndPut(
           BaseCrudService<DTO_OUT, DTO_IN> service,
           Function<DTO_OUT, Long> idExtractor,
           Patchable<DTO_OUT> patchService,
           Putable<DTO_OUT, DTO_IN> putService) {
      super(service, idExtractor);
      this.patchService = patchService;
      this.putService = putService;
   }

   protected ResponseEntity<@NonNull DTO_OUT> patch(
           @Positive(message = "Value must be positive integer and non cero.")
           final long id) {
      final DTO_OUT response = patchService.patch(id);
      return ResponseEntity.ok(response);
   }
   protected ResponseEntity<@NonNull DTO_OUT> put(
           @Positive(message = "Value must be positive integer and non cero.")
           final long id,
           @Valid
           @NotNullObject(message = "Payload must not be null.") final DTO_IN dto) {
      final DTO_OUT response = putService.put(id, dto);
      return ResponseEntity.ok(response);
   }

}
