package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.BaseControllerPutable;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController extends BaseControllerPutable<CustomerEntity, CustomerDtoInput, CustomerDtoOutput> {

   public CustomerController(BaseCrudService<CustomerEntity, CustomerDtoInput, Long> service,
                             AppCacheService<CustomerDtoOutput> cache,
                             BaseMapper<CustomerEntity, CustomerDtoOutput> mapper,
                             CRUDServiceServicePut<CustomerEntity, CustomerDtoInput, Long> putService) {
      super(service, cache, mapper, "customers", CustomerEntity::getId, putService);
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull CustomerDtoOutput>> getPage(
           @RequestParam("page") final int page,
           @RequestParam("size") final int size) {
      return super.getPage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull CustomerDtoOutput> getCustomer(@PathVariable("id") final long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull CustomerDtoOutput> postCustomer(@RequestBody final CustomerDtoInput dto) {
      String LOCATION = "/admin/customers";
      return super.create(dto, LOCATION);
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull CustomerDtoOutput> putCustomer(@PathVariable("id") final Long id,
                                                                          @RequestBody final CustomerDtoInput dto) {
      return super.put(id, dto);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteCustomer(@PathVariable("id") final long id) {
      return super.delete(id);
   }
}
