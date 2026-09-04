package com.jame.dev.gymApp.features.customer.api;

import com.jame.dev.gymApp.features.customer.api.request.CustomerCreateRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerUpdateRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCustomerUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/app/v2/administration/customers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
public class CustomerAdministrationControllerV2 {

   private final CreateCustomerUseCase createCustomerUseCase;
   private final UpdateCustomerUseCase updateCustomerUseCase;

   @PostMapping
   public ResponseEntity<CustomerResponse> createCustomer(
      @RequestBody
      @Valid
      @NotNull(message = "Payload is required.") final CustomerCreateRequest request
   ) {
      final var body = createCustomerUseCase.create(request);
      final URI location = URI.create(
         ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(body.id())
            .toUriString()
            .replace("v2", "v1")
      );
      return ResponseEntity.created(location)
         .body(body);
   }

   @PutMapping("/{id}")
   public ResponseEntity<CustomerResponse> updateCustomer(
      @PathVariable("id")
      @NotNull(message = "resource id is required.") Long id,
      @RequestBody
      @Valid
      @NotNull(message = "Payload is required")
      CustomerUpdateRequest request
   ) {
      final var body = updateCustomerUseCase.update(id, request);
      return ResponseEntity.ok(body);
   }
}
