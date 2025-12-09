package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.BaseController;
import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.service.common.CRUDService;
import com.jame.dev.gymApp.service.in.CustomerService;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/customers")
public class CustomerController extends BaseController<CustomerEntity, CustomerDtoInput, CustomerDtoOutput> {

   private final CustomerService customerService;
   protected CustomerController(CRUDService<CustomerEntity, CustomerDtoInput> service,
                                BaseMapper<CustomerEntity, CustomerDtoOutput> mapper,
                                AppCacheService<CustomerDtoOutput> cache,
                                CustomerService customerService) {
      super(service, mapper, cache, "customers");
      this.customerService = customerService;
   }


   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull CustomerDtoOutput>> getPage(
           @RequestParam("page") final int page,
           @RequestParam("size") final int size){
      return super.getPage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull CustomerDtoOutput> getCustomer(@PathVariable("id") final long id){
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull CustomerDtoOutput> postCustomer(@RequestBody final CustomerDtoInput dto){
      String LOCATION = "/admin/customers";
      return super.create(dto, LOCATION);
   }

   @PatchMapping("/{id}")
   public ResponseEntity<@NonNull CustomerDtoOutput> patchContactCustomer(@PathVariable final Long id,
                                                                   @RequestBody final CustomerDtoInput dto){
      final CustomerEntity customer = customerService.updateContact(id, dto);
      return super.ok(customer);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteCustomer(@PathVariable final long id){
      return super.delete(id);
   }
}
