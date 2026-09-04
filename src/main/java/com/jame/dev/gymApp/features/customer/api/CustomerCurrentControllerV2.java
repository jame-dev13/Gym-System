package com.jame.dev.gymApp.features.customer.api;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.customer.api.request.CustomerUpdateRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCurrentCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCurrentCustomerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/app/v2/customers/current")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Validated
public class CustomerCurrentControllerV2 {

   private final CreateCurrentCustomerUseCase createCurrentCustomerUseCase;
   private final UpdateCurrentCustomerUseCase updateCurrentCustomerUseCase;

   @PostMapping
   public ResponseEntity<CustomerResponse> createCustomer(
      final @AuthenticationPrincipal AuthPrincipal principal
   ) {
      Objects.requireNonNull(createCurrentCustomerUseCase.createCurrent(principal), "Something went wrong.");
      return ResponseEntity
         .created(URI.create(
               ServletUriComponentsBuilder
                  .fromCurrentRequestUri()
                  .build()
                  .toUriString()
                  .replace("v2", "v1")
            )
         )
         .build();
   }

   @PutMapping
   public ResponseEntity<CustomerResponse> updateCustomer(
      final @AuthenticationPrincipal AuthPrincipal principal,
      @RequestBody
      @Valid final CustomerUpdateRequest request
   ) {
      Objects.requireNonNull(
         updateCurrentCustomerUseCase.updateCurrent(principal, request),
         "Something went wrong."
      );
      return ResponseEntity.ok().build();
   }

}
