package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.controller.service.BaseController;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.in.RecoveryRequest;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.in.CustomerRecoverService;
import com.jame.dev.gymApp.service.in.CustomerService;
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
public class CustomerController extends BaseController<CustomerDtoOutput, CustomerDtoInput> {

   private final CustomerRecoverService recoverService;

   public CustomerController(
      final CustomerService service, CustomerRecoverService recoverService) {
      super(service, CustomerDtoOutput::id);
      this.recoverService = recoverService;
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull CustomerDtoOutput>> getCustomerPage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC) final Pageable pageable,
      @RequestParam(required = false, name = "search") String search) {
      return super.getPage(pageable, search);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull CustomerDtoOutput> getCustomer(
      @PathVariable("id")
      @Minimum final long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull CustomerDtoOutput> postCustomer(
      @RequestBody
      @Valid
      @NotNullObject final CustomerDtoInput dto) {
      return super.create(dto);
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull CustomerDtoOutput> updateCustomer(
      @PathVariable("id")
      @Minimum final long id,
      @RequestBody
      @Valid
      @NotNullObject final CustomerDtoInput dto) {
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
