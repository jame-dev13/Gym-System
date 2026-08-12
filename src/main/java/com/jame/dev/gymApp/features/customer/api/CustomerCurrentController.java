package com.jame.dev.gymApp.features.customer.api;

import com.jame.dev.gymApp.features.customer.api.request.CustomerCurrentRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.DeleteCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetCurrentCustomerUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/app/v1/customers/current")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Validated
public class CustomerCurrentController {

   private final CreateCurrentCustomerUseCase createCurrentCustomerUseCase;
   private final GetCurrentCustomerUseCase currentCustomerUseCase;
   private final UpdateCurrentCustomerUseCase updateCurrentCustomerUseCase;
   private final DeleteCurrentCustomerUseCase deleteCurrentCustomerUseCase;

   @PostMapping
   public ResponseEntity<CustomerResponse> register(
      @RequestBody
      @Valid final CustomerCurrentRequest request,
      final Authentication authentication
   ) {
      final CustomerResponse customerResponse = createCurrentCustomerUseCase.createCurrent(authentication, request);
      return ResponseEntity
         .created(ServletUriComponentsBuilder.fromCurrentRequest()
            .build()
            .toUri())
         .body(customerResponse);
   }

   @GetMapping
   public ResponseEntity<CustomerResponse> getCurrent(final Authentication authentication) {
      final CustomerResponse customerResponse = currentCustomerUseCase.getCurrent(authentication);
      return ResponseEntity.ok(customerResponse);
   }

   @PutMapping
   public ResponseEntity<CustomerResponse> updateInfoContact(
      @RequestBody
      @Valid
      @NotNullObject final CustomerCurrentRequest request,
      final Authentication authentication
   ) {
      final CustomerResponse customerResponse = updateCurrentCustomerUseCase.updateCurrent(authentication, request);
      return ResponseEntity.ok(customerResponse);
   }

   @DeleteMapping
   public ResponseEntity<Void> downRegister(final Authentication authentication) {
      deleteCurrentCustomerUseCase.deleteCurrent(authentication);
      return ResponseEntity.noContent().build();
   }
}
