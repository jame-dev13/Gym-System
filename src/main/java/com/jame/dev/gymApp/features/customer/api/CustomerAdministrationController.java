package com.jame.dev.gymApp.features.customer.api;

import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.infrastructure.web.BaseController;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerRecoverService;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/app/v1/administration/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerAdministrationController extends BaseController<CustomerResponse, CustomerRequest> {

   private final CustomerRecoverService recoverService;

   public CustomerAdministrationController(
      final CustomerService service, CustomerRecoverService recoverService) {
      super(service, CustomerResponse::id);
      this.recoverService = recoverService;
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull CustomerResponse>> getCustomerPage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC) final Pageable pageable,
      @RequestParam(required = false, name = "search") String search) {
      return super.getPage(pageable, search);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull CustomerResponse> getCustomer(
      @PathVariable("id")
      @Minimum final long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull CustomerResponse> postCustomer(
      @RequestBody
      @Valid
      @NotNullObject final CustomerRequest dto) {
      return super.create(dto);
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull CustomerResponse> updateCustomer(
      @PathVariable("id")
      @Minimum final long id,
      @RequestBody
      @Valid
      @NotNullObject final CustomerRequest dto) {
      return super.update(id, dto);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteCustomer(
      @PathVariable("id")
      @Minimum final long id) {
      return super.delete(id);
   }

   @PostMapping("/recover")
   public ResponseEntity<Void> recoverCustomer(
      @RequestBody final RecoveryRequest recoveryRequest
   ) {
      final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
         .path("/{id}")
         .buildAndExpand(super.idExtractor)
         .toUri();
      recoverService.recover(recoveryRequest);
      return ResponseEntity.created(uri).build();
   }
}
