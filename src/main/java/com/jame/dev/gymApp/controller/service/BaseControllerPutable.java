package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

@Deprecated
public abstract class BaseControllerPutable<DTO_OUT, DTO_IN>
        extends BaseControllerCommon<DTO_OUT, DTO_IN> {

   private final CRUDServiceServicePut<DTO_OUT, DTO_IN> putService;

   public BaseControllerPutable(
           BaseCrudService<DTO_OUT, DTO_IN> service,
           Function<DTO_OUT, Long> idExtractor,
           CRUDServiceServicePut<DTO_OUT, DTO_IN> putService) {
      super(service, idExtractor);
      this.putService = putService;
   }

   protected ResponseEntity<@NonNull DTO_OUT> put(long id, @NonNull final DTO_IN dto) {
      final DTO_OUT response = putService.put(id, dto);
      return ResponseEntity.ok(response);
   }

}
