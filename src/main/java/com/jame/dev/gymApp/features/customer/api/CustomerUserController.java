package com.jame.dev.gymApp.features.customer.api;

import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.SoftDeleteCustomerByIdUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByEmailCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByIdCustomerUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/app/v1/customers")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Validated
public class CustomerUserController {

   private final CreateCustomerUseCase create;
   private final GetByIdCustomerUseCase getById;
   private final GetByEmailCustomerUseCase getByEmail;
   private final UpdateCustomerUseCase update;
   private final SoftDeleteCustomerByIdUseCase softDelete;

   @PreAuthorize("@customerSecurity.isOwner(#input, authentication)")
   @PostMapping
   public ResponseEntity<CustomerResponse> register(
      @RequestBody
      @Valid final CustomerRequest input) {
      final CustomerResponse customerResponse = create.create(input);
      return ResponseEntity
         .created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(customerResponse.id())
            .toUri())
         .body(customerResponse);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<CustomerResponse> getCurrent(
      @PathVariable("id")
      @Minimum final long id) {
      final CustomerResponse customerResponse = getById.getById(id);
      return ResponseEntity.ok(customerResponse);
   }

   @PreAuthorize("@customerSecurity.isOwner(#email, authentication)")
   @GetMapping("/user/{email}")
   public ResponseEntity<CustomerResponse> getCurrentByEmail(
      @PathVariable("email")
      @EmailValid final String email) {
      final CustomerResponse customerResponse = getByEmail.getByEmail(email);
      return ResponseEntity.ok(customerResponse);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @PutMapping("/{id}")
   public ResponseEntity<CustomerResponse> updateInfoContact(
      @PathVariable("id")
      @Minimum final long id,
      @RequestBody
      @Valid
      @NotNullObject final CustomerRequest input) {
      final CustomerResponse customerResponse = update.update(id, input);
      return ResponseEntity.ok(customerResponse);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> downRegister(
      @PathVariable("id")
      @Minimum final long id) {
      softDelete.softDeleteById(id);
      return ResponseEntity.noContent().build();
   }
}
