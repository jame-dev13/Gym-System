package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.BaseControllerCommon;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController extends BaseControllerCommon<CustomerEntity, CustomerDtoInput, CustomerDtoOutput> {


   public CustomerController(
           final BaseCrudService<CustomerEntity, CustomerDtoInput, Long> service,
           final AppCacheService<CustomerDtoOutput> cache,
           final BaseMapper<CustomerEntity, CustomerDtoOutput> mapper) {
      super(service, cache, mapper, "customers", CustomerEntity::getId);
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
   public ResponseEntity<@NonNull CustomerDtoOutput> updateCustomer(@PathVariable("id") final Long id,
                                                                          @RequestBody final CustomerDtoInput dto) {
      return super.update(id, dto);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteCustomer(@PathVariable("id") final long id) {
      return super.delete(id);
   }
}
