package com.jame.dev.gymApp.controller.routes.app.user;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.ControllerPutIdentifiable;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/v1/customers")
@PreAuthorize("hasRole('USER')")
public class CustomerUsersController extends ControllerPutIdentifiable<CustomerEntity, CustomerDtoInput, CustomerDtoOutput>  {

   public CustomerUsersController(final BaseCrudService<CustomerEntity, CustomerDtoInput, Long> service,
                                  final AppCacheService<CustomerDtoOutput> cache,
                                  final BaseMapper<CustomerEntity, CustomerDtoOutput> mapper,
                                  final CRUDServiceServicePut<CustomerEntity, CustomerDtoInput, Long> putService,
                                  final EmailIdentifiable<CustomerEntity> identifiable) {
      super(service, cache, mapper, "customers", CustomerEntity::getId, putService, identifiable);
   }

   @PostMapping
   public ResponseEntity<CustomerDtoOutput> register(@RequestBody final CustomerDtoInput input){
      return super.create(input, "/app/v1/customers");
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<CustomerDtoOutput> getCurrent(@PathVariable("id") final Long id){
      return super.getOne(id);
   }

   @PreAuthorize("@customerSecurity.isOwner(#email, authentication)")
   @GetMapping("/user/{email}")
   public ResponseEntity<CustomerDtoOutput> getCurrentByEmail(@PathVariable("email") final String email){
      return super.getByEmail(email);
   }

   @PreAuthorize("@customerSecurity.isOwner(#id, authentication) and @authorize.checkIdentity(#input)")
   @PutMapping("/{id}")
   public ResponseEntity<CustomerDtoOutput> updateInfoContact(
           @PathVariable("id") final Long id, @RequestBody final CustomerDtoInput input){
      return super.put(id, input);
   }
}
