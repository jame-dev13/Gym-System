package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public abstract class BaseControllerPatchable<DTO_OUT, DTO_IN>
        extends BaseControllerCommon<DTO_OUT, DTO_IN> {

   private final CRUDServiceServicePatch<DTO_OUT, DTO_IN, Long> patchService;

   public BaseControllerPatchable(
           BaseCrudService<DTO_OUT, DTO_IN, Long> service,
           Function<DTO_OUT, Long> idExtractor,
           CRUDServiceServicePatch<DTO_OUT, DTO_IN, Long> patchService) {
      super(service, idExtractor);
      this.patchService = patchService;
   }


   protected ResponseEntity<@NonNull DTO_OUT> patch(final long id) {
      final DTO_OUT response = patchService.patch(id);
      return ResponseEntity.ok(response);
   }
}
