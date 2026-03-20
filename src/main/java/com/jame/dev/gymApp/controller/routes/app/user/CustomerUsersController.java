package com.jame.dev.gymApp.controller.routes.app.user;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.Minimum;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.controller.service.ControllerIdentifiable;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/v1/customers")
@PreAuthorize("hasRole('USER')")
@Validated
public class CustomerUsersController extends ControllerIdentifiable<CustomerEntity, CustomerDtoOutput, CustomerDtoInput> {

   public CustomerUsersController(
           BaseCrudService<CustomerDtoOutput, CustomerDtoInput> service,
           EmailIdentifiable<CustomerEntity> identifiable,
           BaseMapper<CustomerEntity, CustomerDtoOutput> mapper) {
      super(service, CustomerDtoOutput::id, identifiable, mapper);
   }

   @PreAuthorize("@customerSecurity.isOwner(#input, authentication)")
   @PostMapping
   public ResponseEntity<CustomerDtoOutput> register(
           @RequestBody final CustomerDtoInput input) {
      return super.create(input);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<CustomerDtoOutput> getCurrent(
           @PathVariable("id")
           @Minimum final long id) {
      return super.getOne(id);
   }

   @PreAuthorize("@customerSecurity.isOwner(#email, authentication)")
   @GetMapping("/user/{email}")
   public ResponseEntity<CustomerDtoOutput> getCurrentByEmail(
           @PathVariable("email")
           @EmailValid final String email) {
      return super.getByEmail(email);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @PutMapping("/{id}")
   public ResponseEntity<CustomerDtoOutput> updateInfoContact(
           @PathVariable("id")
           @Minimum final long id,
           @Valid
           @RequestBody
           @NotNullObject final CustomerDtoInput input) {
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
