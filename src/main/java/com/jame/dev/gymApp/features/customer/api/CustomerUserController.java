package com.jame.dev.gymApp.features.customer.api;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.infrastructure.web.ControllerIdentifiable;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.application.contract.BaseService;
import com.jame.dev.gymApp.application.contract.EmailIdentifiable;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/v1/customers")
@PreAuthorize("hasRole('USER')")
@Validated
public class CustomerUserController
   extends ControllerIdentifiable<CustomerEntity, CustomerResponse, CustomerRequest> {

   public CustomerUserController(
           BaseService<CustomerResponse, CustomerRequest> service,
           EmailIdentifiable<CustomerEntity> identifiable,
           BaseMapper<CustomerEntity, CustomerResponse> mapper) {
      super(service, CustomerResponse::id, identifiable, mapper);
   }

   @PreAuthorize("@customerSecurity.isOwner(#input, authentication)")
   @PostMapping
   public ResponseEntity<CustomerResponse> register(
           @RequestBody final CustomerRequest input) {
      return super.create(input);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<CustomerResponse> getCurrent(
           @PathVariable("id")
           @Minimum final long id) {
      return super.getOne(id);
   }

   @PreAuthorize("@customerSecurity.isOwner(#email, authentication)")
   @GetMapping("/user/{email}")
   public ResponseEntity<CustomerResponse> getCurrentByEmail(
           @PathVariable("email")
           @EmailValid final String email) {
      return super.getByEmail(email);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @PutMapping("/{id}")
   public ResponseEntity<CustomerResponse> updateInfoContact(
           @PathVariable("id")
           @Minimum final long id,
           @Valid
           @RequestBody
           @NotNullObject final CustomerRequest input) {
      return super.update(id, input);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> downRegister(
           @PathVariable("id")
           @Minimum final long id) {
      return super.delete(id);
   }
}
