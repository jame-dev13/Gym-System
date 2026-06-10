package com.jame.dev.gymApp.features.customer.api;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.CreateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.RecoverCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.SoftDeleteCustomerByIdUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.mutation.UpdateCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetByIdCustomerUseCase;
import com.jame.dev.gymApp.features.customer.application.usecases.query.GetPageCustomerUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/app/v1/administration/customers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
public class CustomerAdministrationController {
   private final CreateCustomerUseCase create;
   private final GetByIdCustomerUseCase getById;
   private final GetPageCustomerUseCase getPage;
   private final RecoverCustomerUseCase recover;
   private final UpdateCustomerUseCase update;
   private final SoftDeleteCustomerByIdUseCase softDelete;

   @GetMapping
   public ResponseEntity<Page<CustomerResponse>> getCustomerPage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC) final Pageable pageable,
      @RequestParam(required = false, name = "search") final String search) {
      final PageDto<CustomerResponse> pageDto = getPage.getPage(pageable, search);
      final Page<CustomerResponse> page = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(page);
   }

   @GetMapping("/{id}")
   public ResponseEntity<CustomerResponse> getCustomer(
      @PathVariable("id")
      @Minimum final long id) {
      final CustomerResponse customerResponse = getById.getById(id);
      return ResponseEntity.ok(customerResponse);
   }

   @PostMapping
   public ResponseEntity<CustomerResponse> postCustomer(
      @RequestBody
      @Valid
      @NotNullObject final CustomerRequest request) {
      final CustomerResponse customerResponse = create.create(request);
      return ResponseEntity
         .created(ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(customerResponse.id())
            .toUri())
         .body(customerResponse);
   }

   @PutMapping("/{id}")
   public ResponseEntity<CustomerResponse> updateCustomer(
      @PathVariable("id")
      @Minimum final long id,
      @RequestBody
      @Valid
      @NotNullObject final CustomerRequest request) {
      final CustomerResponse customerResponse = update.update(id, request);
      return ResponseEntity.ok(customerResponse);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteCustomer(
      @PathVariable("id")
      @Minimum final long id) {
      softDelete.softDeleteById(id);
      return ResponseEntity.noContent().build();
   }

   @PostMapping("/recover")
   public ResponseEntity<Void> recoverCustomer(
      @RequestBody final RecoveryRequest recoveryRequest
   ) {
      final CustomerResponse customerResponse = recover.recover(recoveryRequest);
      final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
         .path("/{id}")
         .buildAndExpand(customerResponse.id())
         .toUri();
      return ResponseEntity.created(uri).build();
   }
}
