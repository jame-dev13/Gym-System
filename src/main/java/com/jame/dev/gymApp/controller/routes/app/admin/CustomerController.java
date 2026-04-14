package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.controller.service.BaseController;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.in.CustomerService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController extends BaseController<CustomerDtoOutput, CustomerDtoInput> {

   public CustomerController(
           final CustomerService service) {
      super(service, CustomerDtoOutput::id);
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull CustomerDtoOutput>> getPage(
           @RequestParam("page") final int page,
           @RequestParam("size") final int size) {
      return super.getPage(page, size);
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
}
